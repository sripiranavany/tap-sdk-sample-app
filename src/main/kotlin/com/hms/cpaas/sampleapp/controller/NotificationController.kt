package com.hms.cpaas.sampleapp.controller

import com.hms.cpaas.sampleapp.dto.SdkResponse
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class NotificationController {
    companion object {
        private val logger = LoggerFactory.getLogger(NotificationController::class.java)
    }
    @PostMapping(value = ["/sdk/response"])
    fun handleSdkResponse(@RequestBody sdkResponse: SdkResponse) {
        logger.info("SDK Response")
        logger.info(sdkResponse.toString())
        logger.info("*************************************")
    }
}