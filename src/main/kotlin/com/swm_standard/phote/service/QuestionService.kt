package com.swm_standard.phote.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.swm_standard.phote.common.exception.NotFoundException
import com.swm_standard.phote.dto.response.ReadQuestionDetailResponseDto
import com.swm_standard.phote.repository.QuestionRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class QuestionService (private val questionRepository: QuestionRepository) {
    @Transactional
    fun readQuestionDetail(id: UUID): ReadQuestionDetailResponseDto {
        val question = questionRepository.findById(id).orElseThrow { NotFoundException("존재하지 않는 UUID") }

        // string type으로 오는 options를 deserialize
        val objectMapper = ObjectMapper()
        val questionOptionsObject: JsonNode = objectMapper.readTree(question.options)

        return ReadQuestionDetailResponseDto(question, questionOptionsObject)
    }
}