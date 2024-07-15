package com.swm_standard.phote.controller

import com.swm_standard.phote.dto.UserInfoResponseDto
import com.swm_standard.phote.service.GoogleAuthService
import com.swm_standard.phote.common.responsebody.BaseResponse
import com.swm_standard.phote.service.KaKaoAuthService
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val googleAuthService: GoogleAuthService,
    private val kaKaoAuthService: KaKaoAuthService) {

    @Value("\${GOOGLE_CLIENT_ID}")
    lateinit var clientId:String

    @Value("\${REDIRECT_URI}")
    lateinit var redirectUri:String

    @Value("\${KAKAO_REST_API_KEY}")
    lateinit var kakaokey:String

    @Value("\${KAKAO_REDIRECT_URI}")
    lateinit var kakaoRedirectUri:String


    @GetMapping("/google-login")
    fun googleLogin(): RedirectView {
        val redirectView = RedirectView()
        redirectView.url = "https://accounts.google.com/o/oauth2/v2/auth?client_id=$clientId&response_type=code&redirect_uri=$redirectUri&scope=https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email"

        return redirectView
    }

    @GetMapping("/token")
    fun getUserInfo(@RequestParam code: String): BaseResponse<UserInfoResponseDto> {

        val accessToken = googleAuthService.getTokenFromGoogle(code)
        val userInfo = googleAuthService.getUserInfoFromGoogle(accessToken)

        val message = if (userInfo.isMember == false) "회원가입 성공" else "로그인 성공"
        return BaseResponse(msg = message, data = userInfo)
    }

    @GetMapping("/kakao-login")
    fun kakaoLogin(): RedirectView {
        val redirectView = RedirectView()
        redirectView.url = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=$kakaokey&redirect_uri=$kakaoRedirectUri"

        return redirectView
    }

    @GetMapping("/kakao-token")
    fun getKakaoUserInfo(@RequestParam code: String): BaseResponse<UserInfoResponseDto> {

        val accessToken = kaKaoAuthService.getTokenFromKakao(code)
        val userInfo = kaKaoAuthService.getUserInfoFromKakao(accessToken)

        val message = if (userInfo.isMember == false) "회원가입 성공" else "로그인 성공"
        return BaseResponse(msg = message, data = userInfo)
    }


}