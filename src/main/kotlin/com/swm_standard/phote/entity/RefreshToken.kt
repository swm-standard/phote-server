package com.swm_standard.phote.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.redis.core.RedisHash
import java.time.LocalDateTime
import java.util.UUID

@RedisHash(value = "refreshToken", timeToLive = 1296000)
data class RefreshToken(
    @Id
    val refreshToken: UUID,
    val memberId: UUID,
) : Persistable<UUID> {
    @CreatedDate
    var createdDate: LocalDateTime? = null

    override fun isNew() = createdDate == null

    override fun getId(): UUID = refreshToken
}
