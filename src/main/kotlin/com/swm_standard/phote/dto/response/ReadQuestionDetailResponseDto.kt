package com.swm_standard.phote.dto.response
import com.fasterxml.jackson.databind.JsonNode
import com.swm_standard.phote.entity.Question
import java.time.LocalDateTime

data class ReadQuestionDetailResponseDto(
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime? = null,
    val statement: String,
    val image: String? = null,
    val options: JsonNode? = null,
    val answer: String?,
    val category: String,
    val memo: String? = null
) {
    constructor(question: Question, options: JsonNode) : this(
        createdAt = question.createdAt,
        modifiedAt = question.modifiedAt,
        statement = question.statement,
        image = question.image,
        options = options,
        answer = question.answer!!,
        category = question.category,
        memo = question.memo
    )
}
