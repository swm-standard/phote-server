package com.swm_standard.phote.service

import com.swm_standard.phote.repository.MemberRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class MemberService(
    private val memberRepository: MemberRepository,
) {
    fun deleteMember(memberId: UUID): UUID {
        memberRepository.deleteById(memberId)

        return memberId
    }
}
