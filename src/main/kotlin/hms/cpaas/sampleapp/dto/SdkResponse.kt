package hms.cpaas.sampleapp.dto

data class SdkResponse(
    val timeStamp: String,
    val subscriberId: String,
    val applicationId: String,
    val subscriberRequestId: String,
    val version: String,
    val frequency: String,
    val status: String
)
