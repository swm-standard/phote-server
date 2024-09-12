plugins {
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    id("org.jlleitschuh.gradle.ktlint") version "11.1.0"
    kotlin("plugin.jpa") version "1.9.24"
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    id("jacoco")

    // for querydsl
    kotlin("kapt") version "1.9.24"
}

subprojects {
    apply(plugin = "jacoco")

    jacoco {
        toolVersion = "0.8.6"
    }
}

val queryDslVersion: String by extra

group = "com.swm-standard"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.getByName<Jar>("jar") {
    enabled = false
}

repositories {
    mavenCentral()
}

extra["snippetsDir"] = file("build/generated-snippets")

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.mockito:mockito-core:4.6.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.0")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    implementation("org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.9.0-RC")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("com.navercorp.fixturemonkey:fixture-monkey-kotlin:1.0.25")
    implementation("org.bouncycastle:bcpkix-jdk18on:1.75")
    implementation("com.nimbusds:nimbus-jose-jwt:9.12")

    // querydsl
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
    kapt("jakarta.annotation:jakarta.annotation-api")
    kapt("jakarta.persistence:jakarta.persistence-api")

    // swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
}

allOpen {
    annotations("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    outputs.dir(project.extra["snippetsDir"]!!)
}

tasks.asciidoctor {
    inputs.dir(project.extra["snippetsDir"]!!)
    dependsOn(tasks.test)
}

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.jlleitschuh.gradle:ktlint-gradle:11.1.0")
    }
}

// Querydsl
val generated = file("src/main/generated")
tasks.withType<JavaCompile> {
    options.generatedSourceOutputDirectory.set(generated)
}
sourceSets {
    main {
        kotlin.srcDirs += generated
    }
}
tasks.named("clean") {
    doLast {
        generated.deleteRecursively()
    }
}
kapt {
    generateStubs = true
}

tasks.jacocoTestReport {

    dependsOn(tasks.test)

    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)

        html.outputLocation = file(project.layout.buildDirectory.dir("jacoco/index.html"))
        xml.outputLocation = file(project.layout.buildDirectory.dir("jacoco/index.xml"))
    }

    val qDomains = emptyList<String>().toMutableList()

    for (c in 'A'..'Z') {
        val qPattern = "**/*Q$c*"
        qDomains.add("$qPattern*")
    }

    val excludes =
        listOf(
            "**/service/*",
            "**/dto/*",
            "**/controller/*",
            "**/external/*",
            "**/repository/*",
            "**/common/*",
            "**/*PhoteApplication*",
            "**/entity/*RefreshToken*",
        ) + qDomains

    classDirectories.setFrom(
        sourceSets["main"].output.asFileTree.matching {
            exclude(excludes)
        },
    )
    finalizedBy("jacocoTestCoverageVerification")
}

tasks.jacocoTestCoverageVerification {

    violationRules {
        rule {
            element = "CLASS"

            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = 0.50.toBigDecimal()
            }

            limit {
                counter = "METHOD"
                value = "COVEREDRATIO"
                minimum = 0.30.toBigDecimal()
            }

            val qDomains = emptyList<String>().toMutableList()

            for (c in 'A'..'Z') {
                val qPattern = "**.*Q$c*"
                qDomains.add("$qPattern*")
            }

            excludes =
                listOf(
                    "**.service.*",
                    "**.dto.*",
                    "**.controller.*",
                    "**.external.*",
                    "**.repository.*",
                    "**.common.*",
                    "**.*PhoteApplication*",
                    "**.entity.*RefreshToken*",
                ) + qDomains
        }
    }
}
