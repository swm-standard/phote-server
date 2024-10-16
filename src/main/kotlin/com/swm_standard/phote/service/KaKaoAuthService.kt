package com.swm_standard.phote.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.swm_standard.phote.common.authority.JwtTokenProvider
import com.swm_standard.phote.common.module.NicknameGenerator
import com.swm_standard.phote.common.module.ProfileImageGenerator
import com.swm_standard.phote.dto.LoginRequest
import com.swm_standard.phote.dto.MemberInfoResponse
import com.swm_standard.phote.entity.Member
import com.swm_standard.phote.entity.Provider
import com.swm_standard.phote.repository.MemberRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Service
class KaKaoAuthService(
    private val memberRepository: MemberRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val tokenService: TokenService,
) {
    @Value("\${KAKAO_REST_API_KEY}")
    lateinit var kakaokey: String

    fun getTokenFromKakao(request: LoginRequest): String {
        val headers = HttpHeaders()
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8")

        val body: MultiValueMap<String, String> = LinkedMultiValueMap()
        body.add("grant_type", "authorization_code")
        body.add("client_id", kakaokey)
        body.add("redirect_uri", request.redirectUri)
        body.add("code", request.code)

        val kakaoTokenRequest = HttpEntity(body, headers)
        val response =
            RestTemplate().exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String::class.java,
            )

        val responseBody = response.body
        val objectMapper = ObjectMapper()
        val jsonNode = objectMapper.readTree(responseBody)

        return jsonNode["access_token"].asText()
    }

    fun getMemberInfoFromKakao(token: String): MemberInfoResponse {
        val headers = HttpHeaders()
        headers.add("Authorization", "Bearer $token")
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8")

        val kakaoUserInfoRequest = HttpEntity<MultiValueMap<String, String>>(headers)
        val response =
            RestTemplate().exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String::class.java,
            )

        val responseBody = response.body
        val objectMapper = ObjectMapper()
        val jsonNode = objectMapper.readTree(responseBody)

        val email = jsonNode["kakao_account"]["email"].asText()

        var member = memberRepository.findByEmail(email)

        val isMember: Boolean

        if (member == null) {
            val nicknameGenerator = NicknameGenerator()

            member =
                Member(
                    name = nicknameGenerator.randomNickname(),
                    email = email,
                    image = ProfileImageGenerator().imageGenerator(),
                    provider = Provider.KAKAO,
                )

            memberRepository.save(member)

            isMember = false
        } else {
            isMember = true
        }

        val dto =
            MemberInfoResponse(
                name = member.name,
                email = member.email,
                picture = member.image,
                isMember = isMember,
                memberId = member.id,
                accessToken = jwtTokenProvider.createToken(member.id),
            )

        dto.refreshToken = tokenService.generateRefreshToken(member.id)

        return dto
    }
}
