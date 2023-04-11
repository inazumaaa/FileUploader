package com.project.fileuploaderReactive

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class Webclientconfig {

    @Bean
    fun getWebClient():WebClient{
        return WebClient.builder().baseUrl("http://localhost:8080").build()
    }
}