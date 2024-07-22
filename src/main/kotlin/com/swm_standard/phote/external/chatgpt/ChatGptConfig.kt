package com.swm_standard.phote.external.chatgpt

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class ChatGptConfig {
    @Value("\${openai.api.key}")
    lateinit var apiKey: String

    @Bean
    fun template(): RestTemplate {
        val restTemplate = RestTemplate()

        restTemplate.interceptors.add { request, body, execution ->
            request.headers.add("Authorization", "Bearer $apiKey")
            execution.execute(request, body)
        }

        return restTemplate
    }
}
