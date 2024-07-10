package com.swm_standard.phote.repository

import com.swm_standard.phote.entity.QuestionSet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime
import java.util.UUID

interface QuestionSetRepository: JpaRepository<QuestionSet, UUID> {

    fun findAllByWorkbookId(workbookId:UUID): List<QuestionSet>

    @Modifying
    @Query("UPDATE QuestionSet qs SET qs.deletedAt = :deletedAt WHERE qs.question.id = :questionId")
    fun markDeletedByQuestionId(questionId: UUID, deletedAt: LocalDateTime)

}