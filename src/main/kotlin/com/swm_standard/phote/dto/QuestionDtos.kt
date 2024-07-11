package com.swm_standard.phote.dto

import com.fasterxml.jackson.databind.JsonNode
import com.swm_standard.phote.entity.Question
import jakarta.validation.constraints.NotBlank
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.util.*

data class ReadQuestionDetailResponseDto(
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime? = null,
    val statement: String,
    val image: String? = null,
    val options: JsonNode? = null,
    val answer: String,
    val category: String,
    val memo: String? = null
) {
    constructor(question: Question, options: JsonNode) : this(
        createdAt = question.createdAt,
        modifiedAt = question.modifiedAt,
        statement = question.statement,
        image = question.image,
        options = options,
        answer = question.answer,
        category = question.category,
        memo = question.memo
    )

    constructor(question: Question) : this(
        createdAt = question.createdAt,
        modifiedAt = question.modifiedAt,
        statement = question.statement,
        image = question.image,
        answer = question.answer,
        category = question.category,
        memo = question.memo
    )
}

class DeleteQuestionResponseDto (
    val id: UUID,
    val deletedAt: LocalDateTime
)

data class CreateQuestionRequestDto(
    @NotBlank(message = "statement 미입력")
    val statement: String,
    @NotBlank(message = "category 미입력")
    val category: String,
    val options: JsonNode? = null,
    @NotBlank(message = "answer 미입력")
    val answer: String,
    val tags: List<String>? = null,
    val memo: String? = null
)

data class CreateQuestionResponseDto(
    val id: UUID
)