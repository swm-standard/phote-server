package com.swm_standard.phote.dto

import com.swm_standard.phote.entity.Category
import java.util.UUID

data class ReadExamHistoryDetail(
    val statement: String,
    val options: List<String>?,
    val image: String?,
    val category: Category,
    val answer: String,
    val submittedAnswer: String,
    val isCorrect: Boolean,
    val sequence: Int
)

data class ReadExamHistoryDetailResponse(
    val id: UUID,
    val totalCorrect: Int,
    val time: Int,
    val questions: List<ReadExamHistoryDetail>
)
