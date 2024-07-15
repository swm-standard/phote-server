package com.swm_standard.phote.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import java.time.LocalDateTime
import java.util.UUID

data class CreateWorkbookRequest(
    @field:NotBlank(message = "문제집 이름을 입력해주세요.")
    @JsonProperty("title")
    private val _title: String?,

    @JsonProperty("description")
    private val _description: String?,

    @JsonProperty("emoji")
    private val _emoji: String?,
) {
    val title: String get() = _title!!

    val description: String get() = _description ?: ""

    val emoji: String get() = _emoji ?: "📚"
}

data class CreateWorkbookResponse(
    val id: UUID
)

data class DeleteWorkbookResponse(
    val id: UUID,

    val deletedAt: LocalDateTime
)

data class ReadWorkbookDetailResponse(
    val id: UUID,

    val title: String,

    val description: String?,

    val emoji: String,

    val quantity: Int,

    val modifiedAt: LocalDateTime?,
)

data class ReadWorkbookListResponse(
    val id: UUID,

    val title: String,

    val description: String?,

    val emoji: String,

    val quantity: Int,

    val modifiedAt: LocalDateTime?

)

data class AddQuestionsToWorkbookRequest(
    @JsonProperty("questions")
    private val _questions: List<UUID>?
){
    val questions: List<UUID> get() = _questions!!

}

data class DeleteQuestionInWorkbookResponse(
    val workbookId: UUID,

    val questionId: UUID,

    val deletedAt: LocalDateTime
)

data class UpdateQuestionSequenceRequest(
    @field:NotNull
    @JsonProperty("id")
    private var _id: UUID,

    @field:PositiveOrZero
    @JsonProperty("sequence")
    private var _sequence: Int,
){
    val id: UUID get() = _id

    val sequence: Int get() = _sequence
}

data class UpdateQuestionSequenceResponse(
    val id: UUID,
)