package com.swm_standard.phote.controller

import com.swm_standard.phote.dto.UserInfoResponseDto
import com.swm_standard.phote.service.AuthService
import com.swm_standard.phote.common.responsebody.BaseResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @Value("\${GOOGLE_CLIENT_ID}")
    lateinit var clientId:String

    @Value("\${REDIRECT_URI}")
    lateinit var redirectUri:String


    @GetMapping("/google-login")
    fun googleLogin(): RedirectView {
        val redirectView = RedirectView()
        redirectView.url = "https://accounts.google.com/o/oauth2/v2/auth?client_id=$clientId&response_type=code&redirect_uri=$redirectUri&scope=https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email"

        return redirectView
    }

    @GetMapping("/token")
    fun getUserInfo(@RequestParam code: String): BaseResponse<UserInfoResponseDto> {

        val accessToken = authService.getTokenFromGoogle(code)
        val userInfo = authService.getUserInfoFromGoogle(accessToken)

        return BaseResponse(msg = "로그인 성공", data = userInfo)
    }



}