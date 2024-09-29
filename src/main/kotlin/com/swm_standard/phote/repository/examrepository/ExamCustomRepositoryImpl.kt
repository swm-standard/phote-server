package com.swm_standard.phote.repository.examrepository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.swm_standard.phote.entity.QExam.exam
import com.swm_standard.phote.entity.Workbook
import org.springframework.stereotype.Repository

@Repository
class ExamCustomRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : ExamCustomRepository {
    override fun findMaxSequenceByWorkbookId(workbook: Workbook): Int {
        val maxSequence: Int? =
            jpaQueryFactory
                .select(exam.sequence.max())
                .from(exam)
                .where(exam.workbook.eq(workbook))
                .fetchOne()

        if (maxSequence == null) return 0
        return maxSequence
    }
}
