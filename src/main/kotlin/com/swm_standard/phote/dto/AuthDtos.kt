package com.swm_standard.phote.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class GoogleAccessResponseDto(
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

data class UserInfoResponseDto(
    var accessToken: String?,
    val name: String,
    val email: String,
    val picture: String,
    var isMember: Boolean?,
    var userId: UUID?,
)
