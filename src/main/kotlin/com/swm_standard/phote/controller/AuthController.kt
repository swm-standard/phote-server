package com.swm_standard.phote.controller

import com.swm_standard.phote.common.responsebody.BaseResponse
import com.swm_standard.phote.dto.UserInfoResponse
import com.swm_standard.phote.service.GoogleAuthService
import com.swm_standard.phote.service.KaKaoAuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView
import org.springframework.web.util.UriComponentsBuilder

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
    ): ResponseEntity<Void> {
        // 카카오에서 액세스 토큰을 가져옵니다.
        val accessToken = kaKaoAuthService.getTokenFromKakao(code)
        // 액세스 토큰을 사용하여 사용자 정보를 가져옵니다.
        val userInfo = kaKaoAuthService.getUserInfoFromKakao(accessToken)

        // 쿠키에 액세스 토큰을 저장합니다.
        val tokenCookie =
            ResponseCookie
                .from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7일 동안 유효
                .build()

        // 리다이렉트 URL 생성
        val redirectUrl =
            if (userInfo.isMember == false) {
                UriComponentsBuilder
                    .fromUriString("https://pho-te.com/workbook")
                    .build()
                    .toUriString()
            } else {
                UriComponentsBuilder
                    .fromUriString("https://pho-te.com/workbook")
                    .build()
                    .toUriString()
            }

        // 리다이렉트 및 쿠키 설정
        return ResponseEntity
            .status(302)
            .header(HttpHeaders.LOCATION, redirectUrl)
            .header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
            .build()
    }
}
