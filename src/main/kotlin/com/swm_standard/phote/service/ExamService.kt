package com.swm_standard.phote.service

import com.swm_standard.phote.common.exception.NotFoundException
import com.swm_standard.phote.dto.AnswerResponse
import com.swm_standard.phote.dto.ChatGPTRequest
import com.swm_standard.phote.dto.ChatGPTResponse
import com.swm_standard.phote.dto.GradeExamRequest
import com.swm_standard.phote.dto.GradeExamResponse
import com.swm_standard.phote.dto.ReadExamHistoryDetail
import com.swm_standard.phote.dto.ReadExamHistoryDetailResponse
import com.swm_standard.phote.dto.ReadExamHistoryListResponse
import com.swm_standard.phote.dto.SubmittedAnswerRequest
import com.swm_standard.phote.entity.Answer
import com.swm_standard.phote.entity.Category
import com.swm_standard.phote.entity.Exam
import com.swm_standard.phote.entity.Question
import com.swm_standard.phote.repository.AnswerRepository
import com.swm_standard.phote.repository.ExamRepository
import com.swm_standard.phote.repository.MemberRepository
import com.swm_standard.phote.repository.QuestionRepository
import com.swm_standard.phote.repository.WorkbookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import java.util.UUID
import kotlin.jvm.optionals.getOrElse

@Service
class ExamService(
    private val examRepository: ExamRepository,
    private val questionRepository: QuestionRepository,
    private val workbookRepository: WorkbookRepository,
    private val memberRepository: MemberRepository,
    private val answerRepository: AnswerRepository,
    private val template: RestTemplate,
) {
    @Value("\${openai.model.grading}")
    lateinit var model: String

    @Value("\${openai.api.url}")
    lateinit var url: String

    @Transactional(readOnly = true)
    fun readExamHistoryDetail(id: UUID): ReadExamHistoryDetailResponse {
        val exam = examRepository.findById(id).orElseThrow { NotFoundException("examId", "존재하지 않는 examId") }

        val responses =
            buildList {
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
                            sequence = answer.sequence,
                        ),
                    )
                }
            }

        return ReadExamHistoryDetailResponse(
            id = exam.id!!,
            totalCorrect = exam.totalCorrect,
            time = exam.time,
            questions = responses,
        )
    }

    @Transactional(readOnly = true)
    fun readExamHistoryList(workbookId: UUID): List<ReadExamHistoryListResponse> {
        val exams = examRepository.findAllByWorkbookId(workbookId)
        return exams.map { exam ->
            ReadExamHistoryListResponse(
                examId = exam.id!!,
                totalQuantity = exam.calculateTotalQuantity(),
                totalCorrect = exam.totalCorrect,
                time = exam.time,
                sequence = exam.sequence,
            )
        }
    }

    @Transactional
    fun gradeExam(
        workbookId: UUID,
        request: GradeExamRequest,
        memberId: UUID,
    ): GradeExamResponse {
        val workbook =
            workbookRepository
                .findById(
                    workbookId,
                ).getOrElse { throw NotFoundException(fieldName = "workbook") }

        val exam =
            examRepository.save(
                Exam
                    .createExam(
                        memberRepository
                            .findById(
                                memberId,
                            ).getOrElse { throw NotFoundException(fieldName = "member") },
                        workbook,
                        examRepository.findMaxSequenceByWorkbookId(workbook) + 1,
                        request.time,
                    ),
            )

        var totalCorrect = 0

        val response =
            runBlocking {
                request.answers.mapIndexed { index: Int, answer: SubmittedAnswerRequest ->
                    val question: Question =
                        questionRepository.findById(answer.questionId).getOrElse {
                            throw NotFoundException(fieldName = "questionId (${answer.questionId})")
                        }

                    val savingAnswer: Answer =
                        Answer.createAnswer(
                            question = question,
                            submittedAnswer = answer.submittedAnswer,
                            exam = exam,
                            sequence = index + 1,
                        )

                    GlobalScope.launch(Dispatchers.IO) {
                        if (savingAnswer.submittedAnswer == null) {
                            savingAnswer.isCorrect = false
                        } else {
                            when (question.category) {
                                Category.MULTIPLE -> savingAnswer.checkMultipleAnswer()
                                Category.ESSAY ->
                                    savingAnswer.isCorrect =
                                        async { gradeByChatGpt(savingAnswer) }.await()
                            }
                        }
                        if (savingAnswer.isCorrect) {
                            totalCorrect += 1
                        }
                    }

                    val savedAnswer = answerRepository.save(savingAnswer)

                    AnswerResponse(
                        questionId = savedAnswer.question.id,
                        submittedAnswer = savedAnswer.submittedAnswer,
                        correctAnswer = savedAnswer.question.answer,
                        isCorrect = savedAnswer.isCorrect,
                    )
                }
            }

        exam.increaseTotalCorrect(totalCorrect)

        return GradeExamResponse(
            examId = exam.id!!,
            totalCorrect = exam.totalCorrect,
            questionQuantity = response.size,
            answers = response,
        )
    }

    private suspend fun gradeByChatGpt(savingAnswer: Answer): Boolean {
        val chatGptRequest =
            ChatGPTRequest(model, savingAnswer.submittedAnswer!!, savingAnswer.question.answer)

        val chatGPTResponse =
            withContext(Dispatchers.IO) {
                template.postForObject(url, chatGptRequest, ChatGPTResponse::class.java)
            }

        return when (chatGPTResponse!!.choices[0].message.content) {
            "true" -> true
            else -> false
        }
    }
}
