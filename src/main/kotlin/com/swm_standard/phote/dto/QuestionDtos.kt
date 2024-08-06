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
    val modifiedAt: LocalDateTime?,
    val statement: String,
    val image: String?,
    val options: List<String>?,
    val answer: String,
    val category: Category,
    val tags: List<Tag>?,
    val memo: String?,
) {
    constructor(question: Question, options: List<String>?) : this(
        createdAt = question.createdAt,
        modifiedAt = question.modifiedAt,
        statement = question.statement,
        image = question.image,
        options = options,
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
    val imageUrl: String? = null,
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

data class SearchQuestionsResponse(
    val id: UUID,
    val statement: String,
    val options: List<String>?,
    val image: String?,
    val answer: String,
    val category: Category,
    val tags: List<Tag>?,
    val memo: String?,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime?,
) {
    constructor(question: Question, options: List<String>?) : this(
        id = question.id,
        statement = question.statement,
        options = options,
        image = question.image,
        answer = question.answer,
        category = question.category,
        tags = question.tags,
        memo = question.memo,
        createdAt = question.createdAt,
        modifiedAt = question.modifiedAt
    )
}

data class SearchQuestionsToAddResponse(
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime? = null,
    val statement: String,
    val image: String? = null,
    val options: List<String>?,
    val answer: String,
    val category: Category,
    val tags: List<Tag>?,
    val memo: String? = null,
    var isContain: Boolean = false,
)

data class TransformQuestionResponse(
    val content: String,
    val options: List<String>,
    val transformedImageUrl: String?
)
