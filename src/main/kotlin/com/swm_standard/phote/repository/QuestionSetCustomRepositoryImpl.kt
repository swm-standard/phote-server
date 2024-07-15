package com.swm_standard.phote.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.swm_standard.phote.entity.QQuestionSet.questionSet
import com.swm_standard.phote.entity.Workbook
import org.springframework.stereotype.Repository

@Repository
class QuestionSetCustomRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory) : QuestionSetCustomRepository {
    override fun findMaxSequenceByWorkbookId(workbook: Workbook): Int {
        val maxSequence: Int? = jpaQueryFactory.select(questionSet.sequence.max())
            .from(questionSet)
            .where(questionSet.workbook.eq(workbook))
            .fetchOne()

        if (maxSequence == null) return 0
        return maxSequence
    }
}