package com.swm_standard.phote.repository.examrepository

import com.swm_standard.phote.entity.Workbook

interface ExamCustomRepository {
    fun findMaxSequenceByWorkbookId(workbook: Workbook): Int
}
