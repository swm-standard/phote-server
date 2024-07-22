package com.swm_standard.phote.repository

import com.swm_standard.phote.entity.Workbook

interface QuestionSetCustomRepository {

    fun findMaxSequenceByWorkbookId(workbook: Workbook): Int
}
