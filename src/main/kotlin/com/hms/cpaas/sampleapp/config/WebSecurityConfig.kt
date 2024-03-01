package com.hms.cpaas.sampleapp.config

import com.hms.cpaas.sampleapp.repository.UserRepository
import com.hms.cpaas.sampleapp.service.security.UserDetailsServiceImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebSecurityConfig(
    @Value("\${sdk.app.url}") private val baseUrl: String,
    @Value("\${sdk.app.id}") private val appId: String,
    @Value("\${sdk.app.password}") private val appPassword: String,
    @Value("\${ssl.validate.cert}") private val isSslValidateCert: Boolean,
    private var cpaasWebClient: WebClient,
    private var userRepository: UserRepository
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun userDetailsService(): UserDetailsServiceImpl {
        return UserDetailsServiceImpl(baseUrl, appId, appPassword, isSslValidateCert, cpaasWebClient, userRepository)
    }

}