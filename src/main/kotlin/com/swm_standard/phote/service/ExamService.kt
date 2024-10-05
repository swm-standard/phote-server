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
import com.swm_standard.phote.entity.ExamResult
import com.swm_standard.phote.entity.Question
import com.swm_standard.phote.repository.AnswerRepository
import com.swm_standard.phote.repository.ExamResultRepository
import com.swm_standard.phote.repository.MemberRepository
import com.swm_standard.phote.repository.examrepository.ExamRepository
import com.swm_standard.phote.repository.questionrepository.QuestionRepository
import com.swm_standard.phote.repository.workbookrepository.WorkbookRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import java.util.UUID
import kotlin.jvm.optionals.getOrElse

@Service
@Transactional(readOnly = true)
class ExamService(
    private val examRepository: ExamRepository,
    private val questionRepository: QuestionRepository,
    private val workbookRepository: WorkbookRepository,
    private val memberRepository: MemberRepository,
    private val answerRepository: AnswerRepository,
    private val template: RestTemplate,
    private val examResultRepository: ExamResultRepository,
) {
    @Value("\${openai.model.grading}")
    lateinit var model: String

    @Value("\${openai.api.url}")
    lateinit var url: String

    fun readExamHistoryDetail(id: UUID): ReadExamHistoryDetailResponse {
        val examResult = examResultRepository.findByExamId(id) ?: throw NotFoundException(fieldName = "examResult")
        val responses =
            buildList {
                examResult.answers.forEach { answer ->
                    val question = answer.question
                    if (question != null) {
                        add(
                            ReadExamHistoryDetail(
                                statement = question.statement,
                                options = question.options?.let { question.deserializeOptions() },
                                image = question.image,
                                category = question.category,
                                answer = question.answer,
                                submittedAnswer = answer.submittedAnswer,
                                isCorrect = answer.isCorrect,
                                sequence = answer.sequence,
                            ),
                        )
                    }
                }
            }

        // FIXME: 원래 createdAt 은 시험 생성일시임
        return ReadExamHistoryDetailResponse(
            examId = id,
            totalCorrect = examResult.totalCorrect,
            time = examResult.time,
            questions = responses,
            createdAt = examResult.createdAt,
        )
    }

    fun readExamHistoryList(workbookId: UUID): List<ReadExamHistoryListResponse> {
        val exams = examRepository.findAllByWorkbookId(workbookId)
        return exams.map { exam ->
            // FIXME: exam 마다 examResult 를 또 조회해야함 쿼리 개선 필요
            val examResult =
                examResultRepository.findByExamId(exam.id!!) ?: throw NotFoundException(fieldName = "examResult")

            ReadExamHistoryListResponse(
                examId = exam.id!!,
                totalQuantity = examResult.calculateTotalQuantity(),
                totalCorrect = examResult.totalCorrect,
                time = examResult.time,
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
                    ),
            )

        val examResult =
            examResultRepository.save(
                ExamResult.createExamResult(
                    member =
                        memberRepository
                            .findById(
                                memberId,
                            ).orElseThrow { NotFoundException(fieldName = "member") },
                    time = request.time,
                    exam,
                ),
            )

        var totalCorrect = 0

        val response =
            request.answers.mapIndexed { index: Int, answer: SubmittedAnswerRequest ->
                val question: Question =
                    questionRepository.findById(answer.questionId).getOrElse {
                        throw NotFoundException(fieldName = "questionId (${answer.questionId})")
                    }

                val savingAnswer: Answer =
                    Answer.createAnswer(
                        question = question,
                        submittedAnswer = answer.submittedAnswer,
                        examResult = examResult,
                        sequence = index + 1,
                    )

                CoroutineScope(Dispatchers.IO).launch {
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

        examResult.increaseTotalCorrect(totalCorrect)

        return GradeExamResponse(
            examId = exam.id!!,
            totalCorrect = examResult.totalCorrect,
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
