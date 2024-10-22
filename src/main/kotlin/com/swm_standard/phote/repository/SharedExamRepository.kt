package com.swm_standard.phote.repository

import com.swm_standard.phote.entity.SharedExam
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SharedExamRepository : JpaRepository<SharedExam, UUID> {
    fun findAllByMemberId(memberId: UUID): List<SharedExam>
}
