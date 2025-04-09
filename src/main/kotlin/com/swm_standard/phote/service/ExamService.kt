package com.swm_standard.phote.service

import com.swm_standard.phote.common.exception.BadRequestException
import com.swm_standard.phote.common.exception.NotFoundException
import com.swm_standard.phote.dto.AnswerResponse
import com.swm_standard.phote.dto.ChatGPTRequest
import com.swm_standard.phote.dto.ChatGPTResponse
import com.swm_standard.phote.dto.CreateSharedExamRequest
import com.swm_standard.phote.dto.GradeExamRequest
import com.swm_standard.phote.dto.GradeExamResponse
import com.swm_standard.phote.dto.ReadAllSharedExamsResponse
import com.swm_standard.phote.dto.ReadExamHistoryDetail
import com.swm_standard.phote.dto.ReadExamHistoryDetailResponse
import com.swm_standard.phote.dto.ReadExamHistoryListResponse
import com.swm_standard.phote.dto.ReadExamResultDetail
import com.swm_standard.phote.dto.ReadExamResultDetailResponse
import com.swm_standard.phote.dto.ReadExamResultsResponse
import com.swm_standard.phote.dto.ReadExamStudentResult
import com.swm_standard.phote.dto.ReadSharedExamInfoResponse
import com.swm_standard.phote.dto.RegradeExamRequest
import com.swm_standard.phote.dto.RegradeExamResponse
import com.swm_standard.phote.dto.SubmittedAnswerRequest
import com.swm_standard.phote.entity.Answer
import com.swm_standard.phote.entity.Category
import com.swm_standard.phote.entity.Exam
import com.swm_standard.phote.entity.ExamResult
import com.swm_standard.phote.entity.ExamStatus
import com.swm_standard.phote.entity.Member
import com.swm_standard.phote.entity.ParticipationType
import com.swm_standard.phote.entity.Question
import com.swm_standard.phote.entity.SharedExam
import com.swm_standard.phote.entity.Workbook
import com.swm_standard.phote.repository.AnswerRepository
import com.swm_standard.phote.repository.MemberRepository
import com.swm_standard.phote.repository.SharedExamRepository
import com.swm_standard.phote.repository.examrepository.ExamRepository
import com.swm_standard.phote.repository.examresultrepository.ExamResultRepository
import com.swm_standard.phote.repository.questionrepository.QuestionRepository
import com.swm_standard.phote.repository.workbookrepository.WorkbookRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import java.lang.System.currentTimeMillis
import java.time.LocalDateTime
import java.util.UUID
import kotlin.jvm.optionals.getOrElse

private fun SharedExam.checkStatus(): ExamStatus =
    if (this.startTime.isAfter(LocalDateTime.now())) {
        ExamStatus.NOT_STARTED
    } else if (this.endTime.isBefore(LocalDateTime.now())) {
        ExamStatus.COMPLETED
    } else {
        ExamStatus.IN_PROGRESS
    }

private val logger = KotlinLogging.logger {}

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
    private val sharedExamRepository: SharedExamRepository,
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

        return ReadExamHistoryDetailResponse(
            examId = id,
            totalCorrect = examResult.totalCorrect,
            time = examResult.time,
            questions = responses,
            createdAt = examResult.createdAt,
        )
    }

    fun readExamHistoryList(
        workbookId: UUID,
        memberId: UUID,
    ): List<ReadExamHistoryListResponse> {
        val exams =
            examRepository
                .findAllByWorkbookId(workbookId)
                .filter { exam -> !sharedExamRepository.findById(exam.id!!).isPresent }

        val examResults =
            examResultRepository
                .findAllByExamIdListAndMemberId(exams.map { it.id!! }, memberId)
                .also {
                    if (it.size != exams.size) throw NotFoundException(fieldName = "examResult")
                }

        return examResults.map {
            ReadExamHistoryListResponse(
                examId = it.exam.id!!,
                totalCorrect = it.totalCorrect,
                totalQuantity = it.totalQuantity,
                time = it.time,
                sequence = it.exam.sequence,
            )
        }
    }

    fun readExamResults(examId: UUID): ReadExamResultsResponse {
        val exam = examRepository.findById(examId).orElseThrow { NotFoundException(fieldName = "examId") }
        val examResults = examResultRepository.findAllByExamId(examId)

        val responses =
            examResults.map { examResult ->
                ReadExamStudentResult(
                    examResult.member.id,
                    examResult.member.name,
                    examResult.totalCorrect,
                    examResult.time,
                )
            }

        return ReadExamResultsResponse(examId, exam.workbook.quantity, responses)
    }

    fun readExamResultDetail(
        examId: UUID,
        memberId: UUID,
    ): ReadExamResultDetailResponse {
        val examResult =
            examResultRepository.findByExamIdAndMemberId(examId, memberId)
                ?: throw NotFoundException(fieldName = "examResult")
        val responses =
            buildList {
                examResult.answers.forEach { answer ->
                    val question = answer.question
                    if (question != null) {
                        add(
                            ReadExamResultDetail(
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

        val member = memberRepository.findById(memberId).orElseThrow { NotFoundException(fieldName = "memberId") }

        return ReadExamResultDetailResponse(
            examId = examId,
            memberName = member.name,
            totalCorrect = examResult.totalCorrect,
            time = examResult.time,
            questions = responses,
            createdAt = examResult.createdAt,
        )
    }

    @Transactional
    suspend fun gradeExam(
        request: GradeExamRequest,
        memberId: UUID,
    ): GradeExamResponse = withContext(Dispatchers.IO + CoroutineName("gradeExamV2")) {
        val startTime = currentTimeMillis()
        logger.info { "[${coroutineContext[CoroutineName.Key]}] : gradeExam 시작" }

        val member = findMember(memberId)
        val exam = getOrCreateExam(request, memberId, member)
        val examResult = createExamResult(member, request, exam)

        val questions = questionRepository.findAllByIdIn(request.answers.map { it.questionId })
            .associateBy { it.id }

        val answerResults = request.answers.mapIndexed { index, answer ->
            async {
                logger.info { "[${coroutineContext[CoroutineName.Key]}][answer: ${index + 1}] : gradeAnswer 시작" }
                gradeAnswer(questions.getValue(answer.questionId), answer, examResult, index)
            }
        }.awaitAll()
            .let {
                answerRepository.saveAll(it)
            }

        examResult.increaseTotalCorrect(answerResults.count { it.isCorrect })
        examResultRepository.save(examResult)

        logger.info { "[${coroutineContext[CoroutineName]}] : gradeExam 종료" }
        logger.info { "gradeExam 실행 시간: ${currentTimeMillis() - startTime}ms" }
        GradeExamResponse(
            examId = exam.id!!,
            totalCorrect = examResult.totalCorrect,
            questionQuantity = answerResults.size,
            answers = answerResults.map { savedAnswer ->
                AnswerResponse(
                    questionId = savedAnswer.question!!.id,
                    submittedAnswer = savedAnswer.submittedAnswer,
                    correctAnswer = savedAnswer.question!!.answer,
                    isCorrect = savedAnswer.isCorrect,
                )
            }
        )
    }

    @Transactional
    fun regradeExam(
        examId: UUID,
        memberId: UUID,
        request: RegradeExamRequest,
    ): RegradeExamResponse {
        val examResult =
            examResultRepository.findByExamIdAndMemberId(examId, memberId)
                ?: throw NotFoundException(fieldName = "examResult")
        val answer = answerRepository.findByExamResultIdAndQuestionId(examResult.id!!, request.questionId)

        examResult.increaseTotalCorrect(if (request.isCorrect) 1 else -1)
        answer.isCorrect = request.isCorrect == true

        return RegradeExamResponse(request.questionId)
    }

    @Transactional
    fun createSharedExam(
        memberId: UUID,
        request: CreateSharedExamRequest,
    ): UUID {
        SharedExam
            .createSharedExam(
                startTime = request.startTime,
                endTime = request.endTime,
                capacity = request.capacity,
                member = findMember(memberId),
                workbook = findWorkbook(request.workbookId),
                title = request.title,
            ).run {
                val sharedExam = examRepository.save(this)
                return sharedExam.id!!
            }
    }

    fun readAllSharedExams(memberId: UUID): List<ReadAllSharedExamsResponse> {
        val examsAsCreator =
            (
                sharedExamRepository
                    .findAllByMemberId(memberId)
                ).map { exam ->
                ReadAllSharedExamsResponse(
                    examId = exam.id!!,
                    workbookId = exam.workbook.id,
                    creator = exam.member.name,
                    title = exam.title,
                    startTime = exam.startTime,
                    endTime = exam.endTime,
                    capacity = exam.capacity,
                    status = exam.checkStatus(),
                    role = ParticipationType.CREATOR,
                    examineeCount = exam.examineeCount,
                )
            }

        val examsAsExaminee =
            examResultRepository
                .findAllByMemberId(memberId)
                .filter { it.exam is SharedExam }
                .map { examResult ->
                    val sharedExam = examResult.exam as SharedExam
                    ReadAllSharedExamsResponse(
                        examId = examResult.exam.id!!,
                        workbookId = sharedExam.workbook.id,
                        creator = sharedExam.member.name,
                        title = sharedExam.title,
                        startTime = sharedExam.startTime,
                        endTime = sharedExam.endTime,
                        status = sharedExam.checkStatus(),
                        role = ParticipationType.EXAMINEE,
                        totalCorrect = examResult.totalCorrect,
                        questionQuantity = examResult.totalQuantity,
                    )
                }

        return examsAsCreator + examsAsExaminee
    }

    fun readSharedExamInfo(
        examId: UUID,
        memberId: UUID,
    ): ReadSharedExamInfoResponse {
        val sharedExam = sharedExamRepository.findById(examId).orElseThrow { NotFoundException(fieldName = "examId") }
        return ReadSharedExamInfoResponse(
            examId = examId,
            title = sharedExam.title,
            startTime = sharedExam.startTime,
            endTime = sharedExam.endTime,
            capacity = sharedExam.capacity,
            workbookId = sharedExam.workbook.id,
            role = if (sharedExam.member.id == memberId) ParticipationType.CREATOR else ParticipationType.EXAMINEE,
        )
    }

    private fun findWorkbook(workbookId: UUID): Workbook =
        workbookRepository
            .findById(
                workbookId,
            ).getOrElse { throw NotFoundException(fieldName = "workbook") }

    private fun findMember(memberId: UUID): Member =
        memberRepository.findById(memberId).orElseThrow {
            NotFoundException(fieldName = "member")
        }

    private suspend fun gradeAnswer(
        question: Question,
        answer: SubmittedAnswerRequest,
        examResult: ExamResult,
        index: Int
    ): Answer = withContext(Dispatchers.IO) {
        val savingAnswer = Answer.createAnswer(
            question = question,
            submittedAnswer = answer.submittedAnswer,
            examResult = examResult,
            sequence = index + 1
        )
        savingAnswer.isCorrect = when {
            savingAnswer.submittedAnswer == null -> false
            question.category == Category.MULTIPLE -> savingAnswer.checkMultipleAnswer()
            question.category == Category.ESSAY -> gradeByChatGpt(savingAnswer)
            else -> throw BadRequestException(message = "ChatGPT 채점 오류")
        }
        logger.info { "[CoroutineName(###########)][answer: ${index + 1}] : gradeAnswer 종료" }

        savingAnswer
    }

    private suspend fun gradeByChatGpt(savingAnswer: Answer): Boolean {
        val chatGptRequest =
            ChatGPTRequest(model, savingAnswer.submittedAnswer!!, savingAnswer.question!!.answer)

        val chatGPTResponse = withContext(Dispatchers.IO) {
            logger.info { "[${coroutineContext[CoroutineName.Key]}][answer: ${savingAnswer.sequence}] : chatgpt 시작" }
            template.postForObject(url, chatGptRequest, ChatGPTResponse::class.java)
        }

        return when (chatGPTResponse!!.choices[0].message.content) {
            "true" -> true
            else -> false
        }
    }

    private fun createExamResult(
        member: Member,
        request: GradeExamRequest,
        exam: Exam
    ) = examResultRepository.save(
        ExamResult.createExamResult(
            member = member,
            time = request.time,
            exam = exam,
            totalQuantity = request.answers.size,
        ),
    )

    private fun getOrCreateExam(
        request: GradeExamRequest,
        memberId: UUID,
        member: Member
    ) = if (request.workbookId != null) {
        val workbook = findWorkbook(request.workbookId)
        isWorkbookOwner(workbook, memberId)

        examRepository.save(
            Exam
                .createExam(
                    member,
                    workbook,
                    examRepository.findMaxSequenceByWorkbookId(workbook) + 1,
                ),
        )
    } else {
        (
            examRepository
                .findById(
                    checkNotNull(request.examId),
                ).orElseThrow { NotFoundException(fieldName = "exam") }
                as SharedExam
            ).apply {
            validateSubmissionTime()
            increaseExamineeCount()
        }
    }

    private fun isWorkbookOwner(
        workbook: Workbook,
        memberId: UUID,
    ) {
        if (workbook.member.id != memberId) {
            throw BadRequestException(fieldName = "member", "사용자가 소유한 시험이 아닙니다.")
        }
    }
}
