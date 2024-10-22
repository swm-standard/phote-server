package com.swm_standard.phote.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.swm_standard.phote.entity.Category
import com.swm_standard.phote.entity.ExamStatus
import com.swm_standard.phote.entity.ParticipationType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
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
    val examId: UUID,
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

data class ReadExamStudentResult(
    val memberId: UUID,
    val name: String,
    val score: Int,
    val time: Int,
)

data class ReadExamResultsResponse(
    val examId: UUID,
    val totalQuestionCount: Int,
    val students: List<ReadExamStudentResult>,
)

data class ReadExamResultDetail(
    val statement: String,
    val options: List<String>?,
    val image: String?,
    val category: Category,
    val answer: String,
    val submittedAnswer: String?,
    val isCorrect: Boolean,
    val sequence: Int,
)

data class ReadExamResultDetailResponse(
    val examId: UUID,
    val memberName: String,
    val totalCorrect: Int,
    val time: Int,
    val createdAt: LocalDateTime,
    val questions: List<ReadExamResultDetail>,
)

data class GradeExamRequest(
    val time: Int,
    val workbookId: UUID?,
    val examId: UUID?,
    val answers: List<SubmittedAnswerRequest>,
)

data class RegradeExamRequest(
    val questionId: UUID,
    val isCorrect: Boolean,
)

data class RegradeExamResponse(
    val examId: UUID,
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

data class CreateSharedExamRequest(
    @field:NotBlank(message = "시험 이름을 입력해주세요.")
    @JsonProperty("title")
    private val _title: String?,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val workbookId: UUID,
    @field:Positive(message = "수용인원은 양수이어야 합니다.")
    @JsonProperty("capacity")
    private val _capacity: Int,
) {
    val title: String get() = _title!!

    val capacity: Int get() = _capacity
}

data class CreateSharedExamResponse(
    val sharedExamId: UUID,
)

data class ReadAllSharedExamsResponse(
    val examId: UUID,
    val creator: String,
    val title: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val status: ExamStatus,
    val role: ParticipationType,
    val capacity: Int? = null,
    val examineeCount: Int? = null,
    val totalCorrect: Int? = null,
    val questionQuantity: Int? = null,
)

data class ReadSharedExamInfoResponse(
    val examId: UUID,
    val title: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val capacity: Int,
    val workbookId: UUID,
    val role: ParticipationType,
)
