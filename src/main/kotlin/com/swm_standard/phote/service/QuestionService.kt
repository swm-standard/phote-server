package com.swm_standard.phote.service

import com.swm_standard.phote.common.exception.NotFoundException
import com.swm_standard.phote.dto.DeleteQuestionResponseDto
import com.swm_standard.phote.dto.ReadQuestionDetailResponseDto
import com.swm_standard.phote.repository.QuestionRepository
import com.swm_standard.phote.repository.QuestionSetRepository
import com.swm_standard.phote.repository.TagRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class QuestionService(
    private val questionRepository: QuestionRepository,
    private val tagRepository: TagRepository
) {
    @Transactional
    fun readQuestionDetail(id: UUID): ReadQuestionDetailResponseDto {
        val question = questionRepository.findById(id).orElseThrow { NotFoundException("questionId","존재하지 않는 UUID") }
        val tags = tagRepository.findAllByQuestionId(question.id)

        return ReadQuestionDetailResponseDto(question, tags)
    }

    @Transactional
    fun deleteQuestion(id: UUID): DeleteQuestionResponseDto {

        // 존재하지 않는 question id가 아닌지 확인
        questionRepository.findById(id).orElseThrow { NotFoundException("questionId","존재하지 않는 UUID") }

        questionRepository.deleteById(id)

        return DeleteQuestionResponseDto(id, LocalDateTime.now())
    }
}