package hms.cpaas.sampleapp.model

import jakarta.persistence.*

@Entity
@Table(name = "temp_response")
data class TempResponse(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long = 0,
    val timeStamp: String = "",
    val subscriberId: String = "",
    val applicationId: String = "",
    val version: String = "",
    val frequency: String = "",
    val status: String = "",
    val createdAt: String = ""
)
