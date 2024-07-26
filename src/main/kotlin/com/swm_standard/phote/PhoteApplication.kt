package com.swm_standard.phote

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

// @EnableSentry(
// 	dsn = "https://adee77c92dcb5df82124bef4932b8e11@o4507509663596544.ingest.us.sentry.io/4507509678145536",
// 	exceptionResolverOrder = Ordered.LOWEST_PRECEDENCE
// )
@SpringBootApplication
@EnableJpaAuditing
class PhoteApplication

fun main(args: Array<String>) {
    runApplication<PhoteApplication>(*args)
}
