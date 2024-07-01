package com.swm_standard.phote

import io.sentry.Sentry
import io.sentry.spring.jakarta.EnableSentry
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.Ordered

@EnableSentry(
	dsn = "https://adee77c92dcb5df82124bef4932b8e11@o4507509663596544.ingest.us.sentry.io/4507509678145536",
	exceptionResolverOrder = Ordered.LOWEST_PRECEDENCE
)
@SpringBootApplication
class PhoteApplication

fun main(args: Array<String>) {
	runApplication<PhoteApplication>(*args)
}
