package com.swm_standard.phote.repository

import com.swm_standard.phote.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : JpaRepository<Member, Long> {

    fun findByEmail(email: String): Member?
}