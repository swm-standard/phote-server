package com.swm_standard.phote.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.swm_standard.phote.common.exception.AlreadyDeletedException
import com.swm_standard.phote.common.exception.BadRequestException
import com.swm_standard.phote.common.exception.NotFoundException
import com.swm_standard.phote.dto.DeleteQuestionResponseDto
import com.swm_standard.phote.dto.ReadQuestionDetailResponseDto
import com.swm_standard.phote.repository.QuestionRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime
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

    @Transactional
    fun deleteQuestion(id: UUID): DeleteQuestionResponseDto {
        val question = questionRepository.findById(id).orElseThrow { NotFoundException("존재하지 않는 UUID") }
        if (question.deletedAt != null) throw AlreadyDeletedException()

        // deleteAt필드 채우기
        question.deletedAt = LocalDateTime.now()
        questionRepository.save(question)

        return DeleteQuestionResponseDto(id, question.deletedAt!!)
    }
}