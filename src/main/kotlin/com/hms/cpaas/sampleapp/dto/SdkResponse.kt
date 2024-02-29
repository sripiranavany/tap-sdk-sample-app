package com.hms.cpaas.sampleapp.dto

data class SdkResponse(
    val timeStamp: String,
    val subscriberId: String,
    val applicationId: String,
    val version: String,
    val frequency: String,
    val status: String
)
