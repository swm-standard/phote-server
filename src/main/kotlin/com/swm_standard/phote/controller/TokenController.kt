package com.swm_standard.phote.controller

import com.swm_standard.phote.common.responsebody.BaseResponse
import com.swm_standard.phote.dto.RenewAccessTokenResponse
import com.swm_standard.phote.service.TokenService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api")
class TokenController(
    private val tokenService: TokenService,
) {
    @PostMapping("/token")
    fun renewAccessToken(
        @RequestHeader refreshToken: UUID,
    ): BaseResponse<RenewAccessTokenResponse> =
        BaseResponse(
            data = RenewAccessTokenResponse(tokenService.generateAccessToken(refreshToken)),
            msg = "accessToken 갱신 성공",
        )
}
