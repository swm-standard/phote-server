package com.swm_standard.phote.repository

import com.swm_standard.phote.entity.ExamResult
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ExamResultRepository : JpaRepository<ExamResult, UUID> {
    fun findByExamId(examId: UUID): ExamResult?
}
