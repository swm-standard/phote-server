package com.swm_standard.phote.service

import com.swm_standard.phote.common.authority.JwtTokenProvider
import com.swm_standard.phote.common.exception.BadRequestException
import com.swm_standard.phote.common.exception.NotFoundException
import com.swm_standard.phote.dto.MemberInfoResponse
import com.swm_standard.phote.dto.NativeLoginRequest
import com.swm_standard.phote.repository.MemberRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class NativeAuthService(
    private val memberRepository: MemberRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val tokenService: TokenService,
    @Value("\${native-login.password}")
    private val password: String,
) {
    fun getMemberInfoFromNative(request: NativeLoginRequest): MemberInfoResponse {
        validatePassword(request)

        var member = memberRepository.findByEmail(request.email) ?: throw NotFoundException(fieldName = "member")

        val dto =
            MemberInfoResponse(
                accessToken = null,
                name = member.name,
                email = member.email,
                picture = member.image,
                isMember = true,
                memberId = member.id,
            )

        dto.accessToken = jwtTokenProvider.createToken(member.id)
        dto.refreshToken = tokenService.generateRefreshToken(member.id)
        dto.memberId = member.id
        dto.name = member.name

        return dto
    }

    private fun validatePassword(request: NativeLoginRequest) {
        if (request.password != password) {
            throw BadRequestException(fieldName = "password", message = "비밀번호가 일치하지 않습니다.")
        }
    }
}
