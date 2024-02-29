package com.hms.cpaas.sampleapp.model

import jakarta.annotation.Nullable
import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var userId: Long = 0,
    var username: String = "",
    var password: String = "",
    @Nullable var requestId: String = "",
    @Nullable var appId: String = "",
    @Nullable var maskedNumber: String = "",
    @Nullable var status: String = "",
    @Nullable var frequency: String = ""
)
