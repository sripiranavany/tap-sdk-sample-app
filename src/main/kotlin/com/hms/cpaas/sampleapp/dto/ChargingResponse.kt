package com.hms.cpaas.sampleapp.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ChargingResponse(
    val subscriberInfo: List<SubscriberInfo>?,
    val statusDetail: String?,
    @JsonProperty("recipient-address-masked") val recipientAddressMasked: String?,
    val version: String?,
    val statusCode: String
)

data class SubscriberInfo(
    val subscriptionStatus: String?,
    val subscriberId: String?,
    val statusDetail: String?,
    val lastChargedAmount: String?,
    val lastChargedDate: String?,
    val statusCode: String?
)
