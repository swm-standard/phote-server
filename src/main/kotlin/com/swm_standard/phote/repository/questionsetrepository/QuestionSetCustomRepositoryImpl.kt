package com.swm_standard.phote.repository.questionsetrepository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.swm_standard.phote.dto.ReadQuestionsInWorkbookResponse
import com.swm_standard.phote.entity.QQuestion.question
import com.swm_standard.phote.entity.QQuestionSet.questionSet
import com.swm_standard.phote.entity.QuestionSet
import com.swm_standard.phote.entity.Workbook
import org.springframework.stereotype.Repository
import java.util.UUID
import java.util.stream.Collectors

@Repository
class QuestionSetCustomRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : QuestionSetCustomRepository {
    override fun findMaxSequenceByWorkbookId(workbook: Workbook): Int {
        val maxSequence: Int? =
            jpaQueryFactory
                .select(questionSet.sequence.max())
                .from(questionSet)
                .where(questionSet.workbook.eq(workbook))
                .fetchOne()

        if (maxSequence == null) return 0
        return maxSequence
    }

    override fun findAllQuestionsInWorkbook(workbookId: UUID): List<ReadQuestionsInWorkbookResponse> {
        val questionSetIds = toQuestionSetIds(workbookId)

        val questions =
            jpaQueryFactory
                .selectFrom(question)
                .join(question.questionSet, questionSet)
                .fetchJoin()
                .where(questionSet.id.`in`(questionSetIds))
                .orderBy(questionSet.id.asc())
                .fetch()

        var index = 0

        val responses =
            questions.map { q ->
                ReadQuestionsInWorkbookResponse(
                    questionId = q.id,
                    questionSetId = questionSetIds[index++],
                    statement = q.statement,
                    category = q.category,
                    image = q.image,
                    tags = q.tags,
                    options = q.deserializeOptions(),
                )
            }

        return responses
    }

    private fun toQuestionSetIds(workbookId: UUID): List<UUID> {
        val questionSets: MutableList<QuestionSet> =
            jpaQueryFactory
                .selectFrom(questionSet)
                .where(questionSet.id.eq(workbookId))
                .orderBy(questionSet.sequence.asc())
                .fetch()

        return questionSets
            .stream()
            .map { o -> o.id }
            .collect(Collectors.toList())
    }
}
