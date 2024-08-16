package com.swm_standard.phote.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class GoogleAccessResponse(
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("expires_in")
    val expiresIn: Int,
    @JsonProperty("scope")
    val scope: String,
    @JsonProperty("token_type")
    val tokenType: String,
    @JsonProperty("id_token")
    val idToken: String,
)

data class UserInfoResponse(
    var accessToken: String? = null,
    var name: String?,
    val email: String,
    var picture: String,
    var isMember: Boolean? = true,
    var userId: UUID?,
) {
    var refreshToken: UUID? = null
}

data class RenewAccessTokenResponse(
    val accessToken: String,
)

data class LoginRequest(
    val code: String,
    val redirectUri: String,
)
