package com.hms.cpaas.sampleapp.repository

import com.hms.cpaas.sampleapp.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
    fun existsByUsername(username: String): Boolean
    fun findByRequestId(requestId: String): User?
    fun existsByUsernameAndStatusEquals(username: String, status: String): Boolean
}