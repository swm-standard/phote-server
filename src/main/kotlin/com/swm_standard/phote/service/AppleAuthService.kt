package com.swm_standard.phote.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import com.swm_standard.phote.common.authority.JwtTokenProvider
import com.swm_standard.phote.common.module.NicknameGenerator
import com.swm_standard.phote.common.module.ProfileImageGenerator
import com.swm_standard.phote.dto.LoginRequest
import com.swm_standard.phote.dto.MemberInfoResponse
import com.swm_standard.phote.entity.Member
import com.swm_standard.phote.entity.Provider
import com.swm_standard.phote.repository.MemberRepository
import io.jsonwebtoken.JwsHeader
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.security.PrivateKey
import java.security.Security
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Base64
import java.util.Date

@Service
class AppleAuthService(
    private val memberRepository: MemberRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val tokenService: TokenService,
) {
    @Value("\${APPLE_CLIENT_ID}")
    lateinit var clientId: String

    @Value("\${APPLE_KEY_ID}")
    lateinit var keyId: String

    @Value("\${APPLE_TEAM_ID}")
    lateinit var teamId: String

    @Value("\${APPLE_AUTH_URL}")
    lateinit var authUrl: String

    @Value("\${APPLE_K8_KEY}")
    lateinit var k8Key: String

    fun getTokenFromApple(request: LoginRequest): String {
        val headers = HttpHeaders()
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8")

        val body: MultiValueMap<String, String> = LinkedMultiValueMap()
        body.add("client_id", clientId)
        body.add("client_secret", generateClientSecret())
        body.add("code", request.code)
        body.add("grant_type", "authorization_code")
        body.add("redirect_uri", request.redirectUri)

        val appleTokenRequest = HttpEntity(body, headers)
        val response =
            RestTemplate().exchange(
                "https://appleid.apple.com/auth/token",
                HttpMethod.POST,
                appleTokenRequest,
                String::class.java,
            )

        val responseBody = response.body
        val objectMapper = ObjectMapper()
        val jsonNode = objectMapper.readTree(responseBody)

        return jsonNode["id_token"].asText()
    }

    fun getMemberInfoFromApple(token: String): MemberInfoResponse {
        val signedJWT: SignedJWT = SignedJWT.parse(token)
        val getPayload: JWTClaimsSet = signedJWT.getJWTClaimsSet()

        val email = getPayload.getStringClaim("email")

        var member = memberRepository.findByEmail(email)

        val isMember: Boolean

        if (member == null) {
            val nicknameGenerator = NicknameGenerator()

            member =
                Member(
                    name = nicknameGenerator.randomNickname(),
                    email = email,
                    image = ProfileImageGenerator().imageGenerator(),
                    provider = Provider.APPLE,
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

    private fun generateClientSecret(): String {
        val expiration: LocalDateTime = LocalDateTime.now().plusMinutes(5)

        return Jwts
            .builder()
            .setHeaderParam(JwsHeader.KEY_ID, keyId)
            .setIssuer(teamId)
            .setAudience(authUrl)
            .setSubject(clientId)
            .setExpiration(Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant()))
            .setIssuedAt(Date())
            .signWith(getPrivateKey(), SignatureAlgorithm.ES256)
            .compact()
    }

    private fun getPrivateKey(): PrivateKey {
        Security.addProvider(BouncyCastleProvider())
        val converter = JcaPEMKeyConverter().setProvider("BC")

        return try {
            val privateKeyBytes = Base64.getDecoder().decode(k8Key)
            val privateKeyInfo = PrivateKeyInfo.getInstance(privateKeyBytes)
            converter.getPrivateKey(privateKeyInfo)
        } catch (e: Exception) {
            throw RuntimeException("private key생성 실패", e)
        }
    }
}
