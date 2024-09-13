package com.swm_standard.phote.controller

import com.swm_standard.phote.common.responsebody.BaseResponse
import com.swm_standard.phote.dto.LoginRequest
import com.swm_standard.phote.dto.RenewAccessTokenResponse
import com.swm_standard.phote.dto.UserInfoResponse
import com.swm_standard.phote.service.GoogleAuthService
import com.swm_standard.phote.service.KaKaoAuthService
import com.swm_standard.phote.service.AppleAuthService
import com.swm_standard.phote.service.TokenService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Auth API Document")
class AuthController(
    private val googleAuthService: GoogleAuthService,
    private val kaKaoAuthService: KaKaoAuthService,
    private val appleAuthService: AppleAuthService,
    private val tokenService: TokenService,
) {
    @Operation(summary = "google-login", description = "구글 로그인/회원가입")
    @PostMapping("/google-login")
    fun googleLogin(
        @RequestBody request: LoginRequest,
    ): BaseResponse<UserInfoResponse> {
        val accessToken = googleAuthService.getTokenFromGoogle(request)
        val userInfo = googleAuthService.getUserInfoFromGoogle(accessToken)

        val message = if (userInfo.isMember == false) "회원가입 성공" else "로그인 성공"
        return BaseResponse(msg = message, data = userInfo)
    }

    @Operation(summary = "kakao-login", description = "카카오 로그인/회원가입")
    @PostMapping("/kakao-login")
    fun kakaoLogin(
        @RequestBody request: LoginRequest,
    ): BaseResponse<UserInfoResponse> {
        val accessToken = kaKaoAuthService.getTokenFromKakao(request)
        val userInfo = kaKaoAuthService.getUserInfoFromKakao(accessToken)

        val message = if (userInfo.isMember == false) "회원가입 성공" else "로그인 성공"
        return BaseResponse(msg = message, data = userInfo)
    }

    @Operation(summary = "apple-login", description = "애플 로그인/회원가입")
    @PostMapping("/apple-login")
    fun appleLogin(
        @RequestBody request: LoginRequest,
    ): BaseResponse<UserInfoResponse> {
        val idToken = appleAuthService.getTokenFromApple(request)
        val userInfo = appleAuthService.getUserInfoFromApple(idToken)

        val message = if (userInfo.isMember == false) "회원가입 성공" else "로그인 성공"
        return BaseResponse(msg = message, data = userInfo)
    }

    @Operation(summary = "renewAccessToken", description = "refreshToken으로 accessToken 갱신")
    @PostMapping("/refreshtoken")
    fun renewAccessToken(
        @RequestHeader refreshToken: UUID,
    ): BaseResponse<RenewAccessTokenResponse> =
        BaseResponse(
            data = RenewAccessTokenResponse(tokenService.generateAccessToken(refreshToken)),
            msg = "accessToken 갱신 성공",
        )
}
