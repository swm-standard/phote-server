package com.swm_standard.phote.repository

import com.swm_standard.phote.entity.QuestionSet
import org.springframework.data.jpa.repository.JpaRepository

import java.util.UUID

interface QuestionSetRepository: JpaRepository<QuestionSet, UUID> {

    fun findAllByWorkbookId(workbookId:UUID): List<QuestionSet>

    fun findByQuestionIdAndWorkbookId(questionId: UUID, workbookId: UUID): QuestionSet?
}