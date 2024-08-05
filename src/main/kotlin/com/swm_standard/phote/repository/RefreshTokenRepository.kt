package com.swm_standard.phote.repository

import com.swm_standard.phote.entity.RefreshToken
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RefreshTokenRepository : CrudRepository<RefreshToken, UUID> {
    fun findByMemberId(memberId: UUID): RefreshToken?
}
