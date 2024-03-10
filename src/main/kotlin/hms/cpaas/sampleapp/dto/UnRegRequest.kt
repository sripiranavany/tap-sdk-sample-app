package hms.cpaas.sampleapp.dto

data class UnRegRequest(
    val applicationId: String,
    val password: String,
    val subscriberId: String,
    val action: Int
)
