package com.swm_standard.phote.controller

import com.swm_standard.phote.common.responsebody.BaseResponse
import com.swm_standard.phote.dto.UserInfoResponse
import com.swm_standard.phote.service.GoogleAuthService
import com.swm_standard.phote.service.KaKaoAuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Auth API Document")
class AuthController(
    private val googleAuthService: GoogleAuthService,
    private val kaKaoAuthService: KaKaoAuthService,
) {
    @Value("\${KAKAO_REST_API_KEY}")
    lateinit var kakaokey: String

    @Value("\${KAKAO_REDIRECT_URI}")
    lateinit var kakaoRedirectUri: String

    @Operation(summary = "google-login", description = "구글 로그인/회원가입")
    @GetMapping("/google-login")
    fun googleLogin(
        @RequestParam code: String,
    ): BaseResponse<UserInfoResponse> {
        val accessToken = googleAuthService.getTokenFromGoogle(code)
        val userInfo = googleAuthService.getUserInfoFromGoogle(accessToken)

        val message = if (userInfo.isMember == false) "회원가입 성공" else "로그인 성공"
        return BaseResponse(msg = message, data = userInfo)
    }

    @Operation(summary = "kakao-login", description = "카카오 로그인/회원가입")
    @GetMapping("/kakao-login")
    fun kakaoLogin(): RedirectView {
        val redirectView = RedirectView()
        redirectView.url =
            "https://kauth.kakao.com/oauth/authorize?response_type=code&" +
            "client_id=$kakaokey&redirect_uri=$kakaoRedirectUri"

        return redirectView
    }

    @Operation(summary = "kakao-login", description = "카카오 로그인 유저 정보 조회")
    @GetMapping("/kakao-token")
    fun getKakaoUserInfo(
        @RequestParam code: String,
    ): BaseResponse<UserInfoResponse> {
        val accessToken = kaKaoAuthService.getTokenFromKakao(code)
        val userInfo = kaKaoAuthService.getUserInfoFromKakao(accessToken)

        val message = if (userInfo.isMember == false) "회원가입 성공" else "로그인 성공"
        return BaseResponse(msg = message, data = userInfo)
    }
}
