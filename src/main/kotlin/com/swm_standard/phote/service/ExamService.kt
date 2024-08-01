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
        val exam = examRepository.findById(id).orElseThrow { NotFoundException("examId", "존재하지 않는 examId") }

        val responses = buildList {
            exam.answers.forEach { answer ->
                add(
                    ReadExamHistoryDetail(
                        statement = answer.question.statement,
                        options = answer.question.options?.let { answer.question.deserializeOptions() },
                        image = answer.question.image,
                        category = answer.question.category,
                        answer = answer.question.answer,
                        submittedAnswer = answer.submittedAnswer,
                        isCorrect = answer.isCorrect,
                        sequence = answer.sequence
                    )
                )
            }
        }

        return ReadExamHistoryDetailResponse(
            id = exam.id,
            totalCorrect = exam.totalCorrect,
            time = exam.time,
            questions = responses
        )
    }
}
