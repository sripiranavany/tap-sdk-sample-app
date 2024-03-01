package com.hms.cpaas.sampleapp.dto

data class UnRegRequest(
    private val applicationId: String,
    private val password: String,
    private val subscriberId: String,
    private val action: Int
)
