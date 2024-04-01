package hms.cpaas.sampleapp.controller

import hms.cpaas.sampleapp.dto.SdkResponse
import hms.cpaas.sampleapp.model.TempResponse
import hms.cpaas.sampleapp.repository.TempResponseRepository
import hms.cpaas.sampleapp.repository.UserRepository
import hms.cpaas.sampleapp.util.CommonUtils
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class NotificationController(
    private val userRepository: UserRepository,
    private val tempResponseRepository: TempResponseRepository,
    private val commonUtils: CommonUtils
) {
    companion object {
        private val logger = LoggerFactory.getLogger(NotificationController::class.java)
    }

    @PostMapping(value = ["/sdk/response"])
    fun handleSdkResponse(@RequestBody sdkResponse: SdkResponse) {
        if (sdkResponse?.subscriberRequestId !== null) {
            val isUserExistsByRequestId = userRepository.existsByRequestId(sdkResponse.subscriberRequestId)
            if (isUserExistsByRequestId) {
                val user = userRepository.findByRequestId(sdkResponse.subscriberRequestId)
                user?.let {
                    user.status = sdkResponse.status
                    user.appId = sdkResponse.applicationId
                    user.frequency = sdkResponse.frequency
                    user.maskedNumber = sdkResponse.subscriberId
                    userRepository.save(user)
                }
                logger.info("Notification received : $sdkResponse")
                logger.info("user ${user?.username} status updated to ${sdkResponse.status}")
            } else {
                logger.error("user not found with subscriberId ${sdkResponse.subscriberId}")
                val tempResponse = TempResponse(
                    subscriberId = sdkResponse.subscriberId,
                    status = sdkResponse.status,
                    applicationId = sdkResponse.applicationId,
                    frequency = sdkResponse.frequency,
                    createdAt = commonUtils.getCurrentTimestamp(),
                    timeStamp = sdkResponse.timeStamp,
                    version = sdkResponse.version
                )
                tempResponseRepository.save(tempResponse)
                logger.info("temp response saved for subscriberId ${sdkResponse.subscriberId}")
                logger.info("Notification received : $sdkResponse")
            }
        }
        if (sdkResponse?.subscriberId !== null && sdkResponse?.subscriberRequestId === null) {
            val isUserExistsByMaskedNumber = userRepository.existsByMaskedNumber(sdkResponse.subscriberId)
            if (isUserExistsByMaskedNumber) {
                val user = userRepository.findByMaskedNumber(sdkResponse.subscriberId)
                user?.let {
                    user.status = sdkResponse.status
                    user.appId = sdkResponse.applicationId
                    user.frequency = sdkResponse.frequency
                    user.maskedNumber = sdkResponse.subscriberId
                    userRepository.save(user)
                }
                logger.info("Notification received : $sdkResponse")
                logger.info("user ${user?.username} status updated to ${sdkResponse.status}")
            } else {
                logger.error("user not found with subscriberId ${sdkResponse.subscriberId}")
                val tempResponse = TempResponse(
                    subscriberId = sdkResponse.subscriberId,
                    status = sdkResponse.status,
                    applicationId = sdkResponse.applicationId,
                    frequency = sdkResponse.frequency,
                    createdAt = commonUtils.getCurrentTimestamp(),
                    timeStamp = sdkResponse.timeStamp,
                    version = sdkResponse.version
                )
                tempResponseRepository.save(tempResponse)
                logger.info("temp response saved for subscriberId ${sdkResponse.subscriberId}")
                logger.info("Notification received : $sdkResponse")
            }
        }
    }
}