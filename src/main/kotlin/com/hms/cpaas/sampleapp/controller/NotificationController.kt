package com.hms.cpaas.sampleapp.controller

import com.hms.cpaas.sampleapp.dto.SdkResponse
import com.hms.cpaas.sampleapp.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class NotificationController(private val userRepository: UserRepository) {
    companion object {
        private val logger = LoggerFactory.getLogger(NotificationController::class.java)
    }
    @PostMapping(value = ["/sdk/response"])
    fun handleSdkResponse(@RequestBody sdkResponse: SdkResponse) {
        val isUserExists = userRepository.existsByMaskedNumber(sdkResponse.subscriberId)
        if (isUserExists) {
            val user = userRepository.findByMaskedNumber(sdkResponse.subscriberId)
            user?.let {
                user.status = sdkResponse.status
                user.appId = sdkResponse.applicationId
                user.frequency = sdkResponse.frequency
                userRepository.save(user)
            }
            logger.info("user ${user?.username} status updated to ${sdkResponse.status}")
        }
    }
}