package com.swm_standard.phote.service

import com.swm_standard.phote.common.authority.JwtTokenProvider
import com.swm_standard.phote.dto.GoogleAccessResponseDto
import com.swm_standard.phote.dto.UserInfoResponseDto
import com.swm_standard.phote.repository.MemberRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class AuthService(private val memberRepository: MemberRepository, private val jwtTokenProvider: JwtTokenProvider) {
    @Value("\${GOOGLE_CLIENT_ID}")
    lateinit var clientId:String

    @Value("\${GOOGLE_CLIENT_SECRET}")
    lateinit var clientSecret:String

    @Value("\${REDIRECT_URI}")
    lateinit var redirectUri:String

    fun getTokenFromGoogle(code: String): String {
        val restTemplate = RestTemplate()
        val headers = HttpHeaders()
        val params: MutableMap<String, Any> = HashMap()

        val googleTokenRequest: HttpEntity<MutableMap<String, Any>> = HttpEntity(params, headers)
        val response: GoogleAccessResponseDto = restTemplate.exchange(
            "https://oauth2.googleapis.com/token?grant_type=authorization_code&client_id=$clientId&client_secret=$clientSecret&code=$code&redirect_uri=$redirectUri",
            HttpMethod.POST,
            googleTokenRequest,
            GoogleAccessResponseDto::class.java
        ).body!!

        return response.accessToken
    }

    fun getUserInfoFromGoogle(token: String): UserInfoResponseDto {
        val restTemplate = RestTemplate()
        val headers = HttpHeaders()
        val params: MutableMap<String, Any> = HashMap()
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer $token")

        val googleTokenRequest: HttpEntity<MutableMap<String, Any>> = HttpEntity(params, headers)


        val dto: UserInfoResponseDto = restTemplate.exchange(
            "https://www.googleapis.com/userinfo/v2/me",
            HttpMethod.GET,
            googleTokenRequest,
            UserInfoResponseDto::class.java
        ).body!!

        val member = memberRepository.findByEmail(dto.email)

        if (member == null){
            dto.isMember = false
        } else {
            dto.isMember = true
            dto.accessToken = jwtTokenProvider.createToken(dto, member.id)
            dto.userId = member.id
        }

        return dto
    }
}