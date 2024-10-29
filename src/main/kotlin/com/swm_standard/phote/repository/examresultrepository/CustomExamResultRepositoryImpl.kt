package com.swm_standard.phote.repository.examresultrepository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.swm_standard.phote.entity.ExamResult
import com.swm_standard.phote.entity.QExamResult.examResult
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class CustomExamResultRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : CustomExamResultRepository {
    override fun findAllByExamIdListAndMemberId(
        examIdList: List<UUID>,
        memberId: UUID,
    ): List<ExamResult> =
        jpaQueryFactory
            .selectFrom(examResult)
            .where(examResult.exam.id.`in`(examIdList))
            .where(examResult.member.id.eq(memberId))
            .fetch()
}
