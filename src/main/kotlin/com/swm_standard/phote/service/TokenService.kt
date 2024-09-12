package com.swm_standard.phote.service

import com.swm_standard.phote.common.authority.JwtTokenProvider
import com.swm_standard.phote.common.exception.ExpiredTokenException
import com.swm_standard.phote.entity.RefreshToken
import com.swm_standard.phote.repository.RefreshTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.jvm.optionals.getOrElse

@Service
@Transactional(readOnly = true)
class TokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    fun generateAccessToken(refreshToken: UUID): String {
        val refresh =
            refreshTokenRepository.findById(refreshToken).getOrElse {
                throw ExpiredTokenException(fieldName = "refreshToken", message = "로그인이 만료됐습니다. 재로그인해주세요.")
            }

        return jwtTokenProvider.createToken(refresh.memberId)
    }

    @Transactional
    fun generateRefreshToken(memberId: UUID): UUID {
        val refreshToken =
            refreshTokenRepository.findByMemberId(memberId) ?: RefreshToken(UUID.randomUUID(), memberId)
                .let {
                    refreshTokenRepository.save(it)
                }

        return refreshToken.refreshToken
    }
}
