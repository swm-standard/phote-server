package com.swm_standard.phote.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.swm_standard.phote.dto.ReadQuestionsInWorkbookResponse
import com.swm_standard.phote.entity.QQuestionSet.questionSet
import com.swm_standard.phote.entity.Question
import com.swm_standard.phote.entity.QuestionSet
import com.swm_standard.phote.entity.Workbook
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import java.util.UUID
import java.util.stream.Collectors

@Repository
class QuestionSetCustomRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : QuestionSetCustomRepository {
    @PersistenceContext
    private lateinit var em: EntityManager

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
            em
                .createQuery(
                    "select q" +
                        " from Question q" +
                        " join q.questionSet qs" +
                        " join fetch q.tags t" +
                        " where qs.id in :questionSetIds" +
                        " order by qs.sequence",
                    Question::class.java,
                ).setParameter("questionSetIds", questionSetIds)
                .resultList

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
            em
                .createQuery(
                    "select qs from QuestionSet qs" +
                        " where qs.workbook.id = :workbookId" +
                        " order by qs.sequence",
                    QuestionSet::class.java,
                ).setParameter("workbookId", workbookId)
                .resultList

        return questionSets
            .stream()
            .map { o -> o.id }
            .collect(Collectors.toList())
    }
}
