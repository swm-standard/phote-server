package com.swm_standard.phote.repository.examresultrepository

import com.swm_standard.phote.entity.ExamResult
import java.util.UUID

interface CustomExamResultRepository {
    fun findExamAndExamResultByMemberId(memberId: UUID): List<ExamResult>
}
