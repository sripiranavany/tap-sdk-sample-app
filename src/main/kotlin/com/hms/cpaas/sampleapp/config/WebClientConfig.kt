package com.hms.cpaas.sampleapp.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {
    @Bean
    fun cpaasWebClient(): WebClient {
        return WebClient.create()
    }
}

