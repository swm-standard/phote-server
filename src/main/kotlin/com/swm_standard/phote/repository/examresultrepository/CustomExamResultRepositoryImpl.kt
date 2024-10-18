package com.swm_standard.phote.repository.examresultrepository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.swm_standard.phote.entity.ExamResult
import com.swm_standard.phote.entity.QExam.exam
import com.swm_standard.phote.entity.QExamResult.examResult
import java.util.UUID

class CustomExamResultRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : CustomExamResultRepository {
    override fun findExamAndExamResultByMemberId(memberId: UUID): List<ExamResult> =
        jpaQueryFactory
            .selectFrom(examResult)
            .join(examResult.exam, exam)
            .fetchJoin()
            .where(examResult.member.id.eq(memberId))
            .fetch()
}
