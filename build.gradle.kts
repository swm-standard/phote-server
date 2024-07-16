plugins {
	id("org.springframework.boot") version "3.3.1"
	id("io.spring.dependency-management") version "1.1.5"
	id("org.asciidoctor.jvm.convert") version "3.3.2"
	id ("io.sentry.jvm.gradle") version "4.8.0"
	kotlin("plugin.jpa") version "1.9.24"
	kotlin("jvm") version "1.9.24"
	kotlin("plugin.spring") version "1.9.24"

	//for querydsl
	kotlin("kapt") version "1.7.10"
}
val queryDslVersion: String by extra

group = "com.swm-standard"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
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
	implementation ("org.springframework.boot:spring-boot-starter-data-redis")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2")
	runtimeOnly("com.mysql:mysql-connector-j")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	implementation("org.springframework.boot:spring-boot-starter-validation:3.3.0")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
	implementation("org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE")

	//querydsl
	implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
	kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
	kapt("jakarta.annotation:jakarta.annotation-api")
	kapt("jakarta.persistence:jakarta.persistence-api")
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
}

//sentry {
//	// Generates a JVM (Java, Kotlin, etc.) source bundle and uploads your source code to Sentry.
//	// This enables source context, allowing you to see your source
//	// code as part of your stack traces in Sentry.
//	includeSourceContext = true
//
//	org = "swm-standard"
//	projectName = "phote"
//	authToken ="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"
//}

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