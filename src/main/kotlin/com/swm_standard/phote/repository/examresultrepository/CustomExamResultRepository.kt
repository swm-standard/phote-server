package com.swm_standard.phote.repository.examresultrepository

import com.swm_standard.phote.entity.ExamResult
import java.util.UUID

interface CustomExamResultRepository {
    fun findAllByExamIdListAndMemberId(
        examIdList: List<UUID>,
        memberId: UUID,
    ): List<ExamResult>
}
