package com.swm_standard.phote.dto

import com.swm_standard.phote.entity.Category
import java.time.LocalDateTime
import java.util.UUID

data class ReadExamHistoryDetail(
    val statement: String,
    val options: List<String>?,
    val image: String?,
    val category: Category,
    val answer: String,
    val submittedAnswer: String?,
    val isCorrect: Boolean,
    val sequence: Int,
)

data class ReadExamHistoryDetailResponse(
    val id: UUID,
    val totalCorrect: Int,
    val time: Int,
    val questions: List<ReadExamHistoryDetail>,
    val createdAt: LocalDateTime,
)

data class ReadExamHistoryListResponse(
    val examId: UUID,
    val totalQuantity: Int,
    val totalCorrect: Int,
    val time: Int,
    val sequence: Int,
)

data class GradeExamRequest(
    val time: Int,
    val answers: List<SubmittedAnswerRequest>,
)

data class SubmittedAnswerRequest(
    val questionId: UUID,
    val submittedAnswer: String?,
)

data class GradeExamResponse(
    val examId: UUID,
    val totalCorrect: Int,
    val questionQuantity: Int,
    val answers: List<AnswerResponse>,
)

data class AnswerResponse(
    val questionId: UUID,
    val submittedAnswer: String?,
    val correctAnswer: String,
    val isCorrect: Boolean,
)
