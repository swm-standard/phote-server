package com.swm_standard.phote.repository

import com.swm_standard.phote.entity.QuestionSet
import org.springframework.data.jpa.repository.JpaRepository

import java.util.UUID

interface QuestionSetRepository: JpaRepository<QuestionSet, UUID> {

    fun findByQuestionIdAndWorkbookId(questionId: UUID, workbookId: UUID): QuestionSet?

    fun findByWorkbookIdOrderBySequence(workbookId: UUID): List<QuestionSet>
}