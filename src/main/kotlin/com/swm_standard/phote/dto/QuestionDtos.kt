package com.swm_standard.phote.dto

import com.fasterxml.jackson.databind.JsonNode
import com.swm_standard.phote.entity.Category
import com.swm_standard.phote.entity.Question
import com.swm_standard.phote.entity.Tag
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime
import java.util.*

data class ReadQuestionDetailResponse(
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime? = null,
    val statement: String,
    val image: String? = null,
    val options: JsonNode? = null,
    val answer: String,
    val category: Category,
    val tags: List<Tag>? = null,
    val memo: String? = null,
) {
    constructor(question: Question) : this(
        createdAt = question.createdAt,
        modifiedAt = question.modifiedAt,
        statement = question.statement,
        image = question.image,
        options = question.options,
        answer = question.answer,
        tags = question.tags,
        category = question.category,
        memo = question.memo,
    )
}

class DeleteQuestionResponse(
    val id: UUID,
    val deletedAt: LocalDateTime,
)

data class CreateQuestionRequest(
    @field:NotBlank(message = "statement 미입력")
    val statement: String,
    @field:NotNull(message = "category 미입력")
    val category: Category,
    val options: JsonNode? = null,
    @field:NotBlank(message = "answer 미입력")
    val answer: String,
    val tags: List<String>? = null,
    val memo: String? = null,
)

data class CreateQuestionResponse(
    val id: UUID,
)

data class SearchQuestionsToAddResponse(
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime? = null,
    val statement: String,
    val image: String? = null,
    val options: JsonNode? = null,
    val answer: String,
    val category: Category,
    val tags: List<Tag>? = null,
    val memo: String? = null,
    var isContain: Boolean = false,
)

data class TransformQuestionResponse(
    val content: String,
    val options: List<String>,
)
