package com.swm_standard.phote.service

import com.swm_standard.phote.common.exception.InvalidInputException
import com.swm_standard.phote.common.exception.NotFoundException
import com.swm_standard.phote.dto.AddQuestionsToWorkbookRequest
import com.swm_standard.phote.dto.CreateWorkbookRequest
import com.swm_standard.phote.dto.CreateWorkbookResponse
import com.swm_standard.phote.dto.DeleteQuestionInWorkbookResponse
import com.swm_standard.phote.dto.DeleteWorkbookResponse
import com.swm_standard.phote.dto.ReadQuestionsInWorkbookResponse
import com.swm_standard.phote.dto.ReadWorkbookDetailResponse
import com.swm_standard.phote.dto.ReadWorkbookListResponse
import com.swm_standard.phote.dto.ReceiveSharedWorkbookRequest
import com.swm_standard.phote.dto.ReceiveSharedWorkbookResponse
import com.swm_standard.phote.dto.UpdateQuestionSequenceRequest
import com.swm_standard.phote.dto.UpdateWorkbookDetailRequest
import com.swm_standard.phote.dto.UpdateWorkbookDetailResponse
import com.swm_standard.phote.entity.Member
import com.swm_standard.phote.entity.Question
import com.swm_standard.phote.entity.QuestionSet
import com.swm_standard.phote.entity.Workbook
import com.swm_standard.phote.repository.MemberRepository
import com.swm_standard.phote.repository.QuestionRepository
import com.swm_standard.phote.repository.QuestionSetRepository
import com.swm_standard.phote.repository.WorkbookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID
import kotlin.jvm.optionals.getOrElse

@Service
@Transactional(readOnly = true)
class WorkbookService(
    private val workbookRepository: WorkbookRepository,
    private val memberRepository: MemberRepository,
    private val questionSetRepository: QuestionSetRepository,
    private val questionRepository: QuestionRepository,
) {
    @Transactional
    fun createWorkbook(
        request: CreateWorkbookRequest,
        memberId: UUID,
    ): CreateWorkbookResponse {
        val member = getMember(memberId)
        val workbook: Workbook =
            Workbook.createWorkbook(request.title, request.description, member).let {
                workbookRepository.save(it)
            }

        return CreateWorkbookResponse(workbook.id)
    }

    @Transactional
    fun deleteWorkbook(id: UUID): DeleteWorkbookResponse {
        getWorkbook(id)

        workbookRepository.deleteById(id)

        return DeleteWorkbookResponse(id, LocalDateTime.now())
    }

    fun readWorkbookDetail(id: UUID): ReadWorkbookDetailResponse {
        val workbook = getWorkbook(id)

        return ReadWorkbookDetailResponse(
            workbook.id,
            workbook.title,
            workbook.description,
            workbook.emoji,
            workbook.quantity,
            workbook.modifiedAt,
        )
    }

    fun readWorkbookList(memberId: UUID): List<ReadWorkbookListResponse> {
        val member = getMember(memberId)
        val workbooks: List<Workbook> = workbookRepository.findAllByMember(member)

        return workbooks.map { workbook ->
            ReadWorkbookListResponse(
                workbook.id,
                workbook.title,
                workbook.description,
                workbook.emoji,
                workbook.quantity,
                workbook.modifiedAt,
            )
        }
    }

    @Transactional
    fun addQuestionsToWorkbook(
        workbookId: UUID,
        request: AddQuestionsToWorkbookRequest,
    ) {
        val workbook: Workbook = getWorkbook(workbookId)
        var nextSeq = questionSetRepository.findMaxSequenceByWorkbookId(workbook) + 1
        val initialSeq = nextSeq

        request.questions.forEach { questionId ->
            val question: Question =
                questionRepository
                    .findById(questionId)
                    .getOrElse { throw NotFoundException(fieldName = "question", message = "id 를 재확인해주세요.") }

            nextSeq = insertQuestion(question, workbook, nextSeq)
        }

        workbook.increaseQuantity(nextSeq - initialSeq)
    }

    @Transactional
    fun deleteQuestionInWorkbook(
        workbookId: UUID,
        questionId: UUID,
    ): DeleteQuestionInWorkbookResponse {
        val workbook = getWorkbook(workbookId)

        questionSetRepository.findByQuestionIdAndWorkbookId(questionId, workbookId)?.also {
            questionSetRepository.delete(it)

            workbook.decreaseQuantity()

            return DeleteQuestionInWorkbookResponse(workbookId, questionId, LocalDateTime.now())
        }

        throw InvalidInputException("question", message = "문제집에 속해있지 않습니다.")
    }

    @Transactional
    fun updateQuestionSequence(
        workbookId: UUID,
        request: List<UpdateQuestionSequenceRequest>,
    ): UUID {
        val workbook = getWorkbook(workbookId)

        if (!workbook.compareQuestionQuantity(request.size)) {
            throw InvalidInputException(
                fieldName = "question",
                message = "문제집 내 모든 문제를 포함해주세요.",
            )
        }

        request.forEach {
            questionSetRepository
                .findById(it.id)
                .getOrElse { throw InvalidInputException("questionSet") }
                .apply {
                    updateSequence(it.sequence)
                }
        }

        return workbookId
    }

    @Transactional
    fun updateWorkbookDetail(
        workbookId: UUID,
        request: UpdateWorkbookDetailRequest,
    ): UpdateWorkbookDetailResponse {
        val workbook = getWorkbook(workbookId)

        workbook.updateWorkbook(request.title, request.description)

        return UpdateWorkbookDetailResponse(workbookId)
    }

    fun readQuestionsInWorkbook(workbookId: UUID): List<ReadQuestionsInWorkbookResponse> {
        getWorkbook(workbookId)

        val questionSets: List<QuestionSet> = questionSetRepository.findByWorkbookIdOrderBySequence(workbookId)

        return questionSets.map { set ->
            ReadQuestionsInWorkbookResponse(
                set.id,
                set.question.id,
                set.question.statement,
                set.question.options,
                set.question.image,
                set.question.category,
                set.question.tags,
            )
        }
    }

    @Transactional
    fun receiveSharedWorkbook(
        request: ReceiveSharedWorkbookRequest,
        memberId: UUID,
    ): ReceiveSharedWorkbookResponse {
        val workbook = getWorkbook(request.workbookId)
        val member = getMember(memberId)

        val sharedWorkbook =
            Workbook
                .createSharedWorkbook(workbook, member)
                .let { workbookRepository.save(it) }

        val questionSets = workbook.questionSet
        requireNotNull(questionSets)
        val map = questionSets.map { q -> q.question }

        val sharedQuestions = questionRepository.saveAll(Question.createSharedQuestions(map, member))

        var nextSeq = 0

        sharedQuestions.forEach { question ->
            nextSeq = insertQuestion(question, sharedWorkbook, nextSeq)
        }

        sharedWorkbook.increaseQuantity(nextSeq)

        return ReceiveSharedWorkbookResponse(sharedWorkbook.id)
    }

    private fun getWorkbook(workbookId: UUID): Workbook {
        val workbook: Workbook =
            workbookRepository
                .findById(workbookId)
                .orElseThrow { NotFoundException(fieldName = "workbook", message = "id 를 재확인해주세요.") }
        return workbook
    }

    private fun getMember(memberId: UUID): Member {
        val member =
            memberRepository.findById(memberId).getOrElse {
                throw InvalidInputException(fieldName = "memberId", message = "id 를 재확인해주세요.")
            }
        return member
    }

    private fun insertQuestion(
        question: Question,
        workbook: Workbook,
        sequence: Int,
    ): Int {
        if (!questionSetRepository.existsByQuestionIdAndWorkbookId(question.id, workbook.id)
        ) {
            questionSetRepository.save(QuestionSet.createSequence(question, workbook, sequence))
            return sequence + 1
        }

        return sequence
    }
}
