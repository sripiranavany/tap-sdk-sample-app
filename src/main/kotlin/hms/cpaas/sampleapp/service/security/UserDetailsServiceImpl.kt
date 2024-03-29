package hms.cpaas.sampleapp.service.security

import hms.cpaas.sampleapp.dto.ChargingRequest
import hms.cpaas.sampleapp.dto.ChargingResponse
import hms.cpaas.sampleapp.repository.UserRepository
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

class UserDetailsServiceImpl(
    @Value("\${sdk.app.url}") private val baseUrl: String,
    @Value("\${sdk.app.id}") private val appId: String,
    @Value("\${sdk.app.password}") private val appPassword: String,
    @Value("\${ssl.validate.cert}") private val isSslValidateCert: Boolean,
    @Value("\${sdk.app.charge}") private val getChargingDetails: String,
    private var cpaasWebClient: WebClient,
    private var userRepository: UserRepository
) : UserDetailsService {

    companion object {
        private val logger = LoggerFactory.getLogger(UserDetailsServiceImpl::class.java)
    }

    init {
        //        This check is to bypass the SSL certificate validation, do not use this in production
        val httpClient = if (!isSslValidateCert) {
            val sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build()

            HttpClient.create()
                .secure { spec -> spec.sslContext(sslContext) }
        } else {
            HttpClient.create()
        }
        cpaasWebClient = WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .baseUrl(baseUrl)
            .build()
    }

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found")

        if (user.status?.isNotEmpty() == true && !user.status.equals("UNREGISTERED", ignoreCase = true)) {
            val chargingRequest =
                ChargingRequest(appId, appPassword, "tel: ${user.maskedNumber}")

            logger.info("Charging request: $chargingRequest")
            val response = cpaasWebClient.post()
                .uri(getChargingDetails)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(chargingRequest)
                .retrieve()
                .onStatus({ it.isError }) { clientResponse ->
                    throw UsernameNotFoundException("Error calling charging service: ${clientResponse.statusCode()}")
                }
                .bodyToMono(ChargingResponse::class.java)
                .block() // This forces the operation to be synchronous

            if (response != null) {
                logger.info("Charging call response: $response")
                if (response.statusCode == "S1000" && response.subscriberInfo?.all { it.subscriptionStatus == "REGISTERED" } == true) {
                    logger.info("Charging info OK for user: $username")
                    if (user.status != response.subscriberInfo?.firstOrNull()?.subscriptionStatus) {
                        logger.info("User status changed from ${user.status} to ${response.subscriberInfo?.firstOrNull()?.subscriptionStatus}")
                        user.status = response.subscriberInfo?.firstOrNull()?.subscriptionStatus.toString()
                        userRepository.save(user)
                    }
                    return User(user.username, user.password, emptyList())
                } else {
                    user.status = response.subscriberInfo?.firstOrNull()?.subscriptionStatus ?: "UNREGISTERED"
                    userRepository.save(user)
                    logger.error("Charging service indicates user not active: $username")
                    throw UsernameNotFoundException("User not active")
                }
            }
            logger.error("Charging service did not return a response")
            throw UsernameNotFoundException("User not active")
        } else {
            logger.error("User not active: $username")
            throw UsernameNotFoundException("User not active")
        }
    }


}