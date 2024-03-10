package hms.cpaas.sampleapp.util

import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@Component
class CommonUtils {
    fun generateRequestId(): String {
        var currenttimestamp = System.nanoTime()
        val randomnumber = Random.nextInt(9000) + 1000
        val combined = "$currenttimestamp$randomnumber"
        return combined.takeLast(15)
    }

    fun sha512(signature: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-512")
        val digestByest = messageDigest.digest(signature.toByteArray())
        return digestByest.fold("") { str, it -> str + "%02x".format(it) }
    }

    fun getCurrentTimestamp(): String {
        val currentTime = Instant.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .withZone(ZoneOffset.UTC)
        return formatter.format(currentTime)
    }
}