package com.swm_standard.phote.repository.questionsetrepository

import com.swm_standard.phote.dto.ReadQuestionsInWorkbookResponse
import com.swm_standard.phote.entity.Workbook
import java.util.UUID

interface QuestionSetCustomRepository {
    fun findMaxSequenceByWorkbookId(workbook: Workbook): Int

    fun findAllQuestionsInWorkbook(workbookId: UUID): List<ReadQuestionsInWorkbookResponse>
}
