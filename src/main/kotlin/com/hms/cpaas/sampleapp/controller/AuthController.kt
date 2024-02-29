package com.hms.cpaas.sampleapp.controller

import com.hms.cpaas.sampleapp.model.User
import com.hms.cpaas.sampleapp.repository.TempResponseRepository
import com.hms.cpaas.sampleapp.repository.UserRepository
import com.hms.cpaas.sampleapp.util.CommonUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView

@Controller
class AuthController(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val commonUtils: CommonUtils,
    private val tempResponseRepository: TempResponseRepository,
    @Value("\${sdk.api.key}") private val apiKey: String,
    @Value("\${sdk.api.secret}") private val apiSecret: String,
    @Value("\${sdk.api.url}") private val apiUrl: String,
    @Value("\${sdk.redirect.url}") private val sdkRdirectUrl: String
) {

    companion object {
        private val logger = LoggerFactory.getLogger(AuthController::class.java)
    }

    @GetMapping(value = ["/login"])
    fun login(request: HttpServletRequest): ModelAndView {
        val modelAndView = ModelAndView("auth/login")
        val errorMessage:String? = request.getSession(false)?.getAttribute("loginError")?.toString()
        if(errorMessage != null){
            modelAndView.addObject("loginError",errorMessage)
            request.session.removeAttribute("loginError")
        }
        return modelAndView
    }

    @GetMapping(value = ["/signup"])
    fun signup(): ModelAndView {
        val user = User()
        return ModelAndView("auth/signup", "user", user)
    }

    @GetMapping(value = ["/"])
    fun home(): ModelAndView {
//        need to check the subscription related endpoints
        return ModelAndView("home")
    }

    @PostMapping(value = ["/signup"])
    fun registerUser(@ModelAttribute("user") @Valid user: User, bindingResult: BindingResult): Any {
        if (bindingResult.hasErrors()) {
            return ModelAndView("auth/signup")
        }
        if (userRepository.existsByUsernameAndStatusEquals(user.username, "REGISTERED")) {
            bindingResult.rejectValue("username", "error.user", "This username is already taken")
            return ModelAndView("auth/login")
        } else {
            if (userRepository.existsByUsername(user.username)) {
                var tempUser = userRepository.findByUsername(user.username)
                if (tempUser != null) {
                    user.userId = tempUser.userId
                }
                user.username = tempUser?.username.toString()
            }
            val requestId = commonUtils.generateRequestId()
            user.password = passwordEncoder.encode(user.password)
            user.requestId = requestId
            userRepository.save(user)

            val requestTime = commonUtils.getCurrentTimestamp()
            val signature = "$apiKey|$requestTime|$apiSecret"
            val encryptedSignature = commonUtils.sha512(signature)

            val sdkRedirectUrl =
                "$apiUrl?apiKey=$apiKey&requestId=$requestId&requestTime=$requestTime&signature=$encryptedSignature&redirectUrl=$sdkRdirectUrl"
            return RedirectView(sdkRedirectUrl)
        }
    }

    @GetMapping(value = ["/sdk/", "/sdk"])
    fun handleSdkRequest(
        @RequestParam("subscriptionStatus", required = false) subscriptionStatus: String?,
        @RequestParam("subscriberId", required = false) subscriberId: String?,
        @RequestParam("requestId", required = false) requestId: String?,
        @RequestParam(value = "continue", required = false) continueParam: String?
    ): ModelAndView {
        logger.info("Subscription Status: $subscriptionStatus")
        logger.info("Subscriber Id: $subscriberId")
        logger.info("Request Id: $requestId")
        logger.info("Continue: $continueParam")
        if (subscriptionStatus.equals("S1000")) {
            val user = requestId?.let { userRepository.findByRequestId(it) }
            if (user != null) {
                if (subscriberId != null) {
                    val tempResponse = subscriberId?.let { tempResponseRepository.findBySubscriberId(it) }
                    if (tempResponse != null) {
                        logger.info("temp response found for subscriberId ${tempResponse.subscriberId}")
                        user.maskedNumber = tempResponse.subscriberId
                        user.appId = tempResponse.applicationId
                        user.frequency = tempResponse.frequency
                        user.status = tempResponse.status
                        userRepository.save(user)
                        tempResponseRepository.delete(tempResponse)
                    } else {
                        logger.info("temp response not found for subscriberId $subscriberId")
                        user.maskedNumber = subscriberId
                        userRepository.save(user)
                    }
                }
            }
        }
        return ModelAndView("auth/login")
    }
}