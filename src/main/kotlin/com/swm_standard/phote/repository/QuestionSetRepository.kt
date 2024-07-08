package com.swm_standard.phote.repository

import com.swm_standard.phote.entity.QuestionSet
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface QuestionSetRepository: JpaRepository<QuestionSet, Long> {

    fun findAllByWorkbookId(workbookId:UUID): List<QuestionSet>


}