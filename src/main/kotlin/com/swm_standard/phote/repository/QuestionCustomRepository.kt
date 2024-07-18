package com.swm_standard.phote.repository

import com.swm_standard.phote.dto.SearchQuestionsToAddResponse
import com.swm_standard.phote.entity.Question
import java.util.*

interface QuestionCustomRepository {
    fun searchQuestionsList(memberId: UUID, tags: List<String>?, keywords: List<String>?): List<Question>

    fun searchQuestionsToAddList(memberId: UUID, workbookId: UUID, tags: List<String>?, keywords: List<String>?): List<SearchQuestionsToAddResponse>
}