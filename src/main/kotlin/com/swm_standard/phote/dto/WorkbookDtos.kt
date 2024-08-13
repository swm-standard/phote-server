package com.swm_standard.phote.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.swm_standard.phote.entity.Category
import com.swm_standard.phote.entity.Tag
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.time.LocalDateTime
import java.util.UUID

data class CreateWorkbookRequest(
    @field:NotBlank(message = "문제집 이름을 입력해주세요.")
    @JsonProperty("title")
    private val _title: String?,
    @JsonProperty("description")
    private val _description: String?,
) {
    val title: String get() = _title!!

    val description: String get() = _description ?: ""
}

data class CreateWorkbookResponse(
    val workbookId: UUID,
)

data class DeleteWorkbookResponse(
    val workbookId: UUID,
    val deletedAt: LocalDateTime,
)

data class ReadWorkbookDetailResponse(
    val workbookId: UUID,
    val title: String,
    val description: String?,
    val emoji: String,
    val quantity: Int,
    val modifiedAt: LocalDateTime?,
)

data class ReadWorkbookListResponse(
    val workbookId: UUID,
    val title: String,
    val description: String?,
    val emoji: String,
    val quantity: Int,
    val modifiedAt: LocalDateTime?,
)

data class AddQuestionsToWorkbookRequest(
    @JsonProperty("questions")
    private val _questions: List<UUID>?,
) {
    val questions: List<UUID> get() = _questions!!
}

data class DeleteQuestionInWorkbookResponse(
    val workbookId: UUID,
    val questionId: UUID,
    val deletedAt: LocalDateTime,
)

data class UpdateQuestionSequenceRequest(
    @JsonProperty("questionSetId")
    private val _questionSetId: UUID?,
    @field:Positive(message = "sequence는 1 이상의 정수만 가능합니다.")
    @JsonProperty("sequence")
    private val _sequence: Int?,
) {
    val questionSetId: UUID get() = _questionSetId!!

    val sequence: Int get() = _sequence!!
}

data class UpdateQuestionSequenceResponse(
    val workbookId: UUID,
)

data class UpdateWorkbookDetailRequest(
    @field:NotBlank(message = "title 미입력")
    val title: String,
    @field:NotNull(message = "description 미입력")
    val description: String?,
)

data class UpdateWorkbookDetailResponse(
    val workbookId: UUID,
)

data class ReadQuestionsInWorkbookResponse(
    val questionSetId: UUID,
    val questionId: UUID,
    val statement: String,
    val options: JsonNode?,
    val image: String?,
    val category: Category,
    val tags: List<Tag>,
)

data class ReceiveSharedWorkbookRequest(
    val workbookId: UUID
)

data class ReceiveSharedWorkbookResponse(
    val workbookId: UUID,
)
