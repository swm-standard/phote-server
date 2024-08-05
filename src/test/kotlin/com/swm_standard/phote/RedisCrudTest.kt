package com.swm_standard.phote

import com.swm_standard.phote.entity.RefreshToken
import com.swm_standard.phote.repository.RefreshTokenRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RedisCrudTest {
    @Autowired
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    @Test
    fun redis_save() {
        val refreshToken = RefreshToken(refreshToken = UUID.randomUUID(), memberId = UUID.randomUUID())

        val save = refreshTokenRepository.save(refreshToken)

        assert(save.memberId == refreshToken.memberId)
    }
}
