package com.hms.cpaas.sampleapp.service.security

import com.hms.cpaas.sampleapp.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

class UserDetailsServiceImpl : UserDetailsService {
    companion object {
        private val logger = LoggerFactory.getLogger(UserDetailsServiceImpl::class.java)
    }

    @Autowired
    private lateinit var userRepository: UserRepository
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username) ?: throw UsernameNotFoundException("User not found")
        if (user.status.equals("REGISTERED", ignoreCase = true)) {
            logger.info("user found and active")
            return User(user.username, user.password, emptyList())
        } else {
            logger.error("user not active")
            throw UsernameNotFoundException("User not active")
        }
    }
}