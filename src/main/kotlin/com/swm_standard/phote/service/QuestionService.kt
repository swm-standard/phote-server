package com.swm_standard.phote.service

import com.swm_standard.phote.common.exception.NotFoundException
import com.swm_standard.phote.dto.response.ReadQuestionDetailResponseDto
import com.swm_standard.phote.repository.QuestionRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class QuestionService (private val questionRepository: QuestionRepository) {
    @Transactional
    fun readQuestionDetail(questionUUID: UUID): ReadQuestionDetailResponseDto {
        val question = questionRepository.findById(questionUUID)
            ?: throw NotFoundException("존재하지 않는 UUID")

        return ReadQuestionDetailResponseDto(question)
    }
}