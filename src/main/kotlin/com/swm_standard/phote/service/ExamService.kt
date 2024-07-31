package com.swm_standard.phote.service

import com.swm_standard.phote.common.exception.NotFoundException
import com.swm_standard.phote.dto.ReadExamHistoryDetail
import com.swm_standard.phote.dto.ReadExamHistoryDetailResponse
import com.swm_standard.phote.repository.ExamRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ExamService(
    private val examRepository: ExamRepository
) {
    @Transactional(readOnly = true)
    fun readExamHistoryDetail(id: Long): ReadExamHistoryDetailResponse {
        val exam = examRepository.findById(id).orElseThrow{ NotFoundException("examId", "존재하지 않는 examId") }
        val responses = mutableListOf<ReadExamHistoryDetail>()

        exam.answers.map {
            val options = it.question.options?.let { options ->
                it.question.deserializeOptions()
            }
            responses.add(
                ReadExamHistoryDetail(
                    statement = it.question.statement,
                    options = options,
                    image = it.question.image,
                    category = it.question.category,
                    answer = it.question.answer,
                    submittedAnswer = it.submittedAnswer,
                    isCorrect = it.isCorrect,
                    sequence = it.sequence
                )
            )
        }

        return ReadExamHistoryDetailResponse(id = exam.id, totalCorrect = exam.totalCorrect, time = exam.time, questions = responses)
    }
}
