package com.swm_standard.phote.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.swm_standard.phote.common.exception.AlreadyDeletedException
import com.swm_standard.phote.common.exception.BadRequestException
import com.swm_standard.phote.common.exception.NotFoundException
import com.swm_standard.phote.dto.DeleteQuestionResponseDto
import com.swm_standard.phote.dto.ReadQuestionDetailResponseDto
import com.swm_standard.phote.repository.QuestionRepository
import com.swm_standard.phote.repository.QuestionSetRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class QuestionService (
    private val questionRepository: QuestionRepository,
    private val questionSetRepository: QuestionSetRepository) {
    @Transactional
    fun readQuestionDetail(id: UUID): ReadQuestionDetailResponseDto {
        val question = questionRepository.findById(id).orElseThrow { NotFoundException("존재하지 않는 UUID") }

        // options가 있는 객관식일 경우
        if (question.options != null) {
            // string type으로 오는 options를 deserialize
            val objectMapper = ObjectMapper()
            val questionOptionsObject: JsonNode = objectMapper.readTree(question.options)

            return ReadQuestionDetailResponseDto(question, questionOptionsObject)
        }

        // options가 없는 주관식일 경우
        return ReadQuestionDetailResponseDto(question)
    }

    @Transactional
    fun deleteQuestion(id: UUID): DeleteQuestionResponseDto {
        val question = questionRepository.findById(id).orElseThrow { NotFoundException("존재하지 않는 UUID") }
        if (question.deletedAt != null) throw AlreadyDeletedException()

        val now = LocalDateTime.now()

        // workbook과의 관계 끊어주기
        questionSetRepository.markDeletedByQuestionId(id, now)

        // deletedAt필드 채우기
        question.deletedAt = now
        questionRepository.save(question)

        return DeleteQuestionResponseDto(id, question.deletedAt!!)
    }
}