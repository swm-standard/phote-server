package com.swm_standard.phote.service

import com.swm_standard.phote.common.exception.AlreadyExistedException
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
import com.swm_standard.phote.dto.UpdateQuestionSequenceRequest
import com.swm_standard.phote.dto.UpdateWorkbookDetailRequest
import com.swm_standard.phote.dto.UpdateWorkbookDetailResponse
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
        val member = memberRepository.findById(memberId).orElseThrow { NotFoundException(fieldName = "member") }
        val workbook = Workbook.createWorkbook(request.title, request.description, member)
        return CreateWorkbookResponse(workbook.id)
    }

    @Transactional
    fun deleteWorkbook(id: UUID): DeleteWorkbookResponse {
        workbookRepository.findById(id).orElseThrow { NotFoundException("workbookId", "존재하지 않는 workbook") }

        workbookRepository.deleteById(id)

        return DeleteWorkbookResponse(id, LocalDateTime.now())
    }

    @Transactional(readOnly = true)
    fun readWorkbookDetail(id: UUID): ReadWorkbookDetailResponse {
        val workbook = workbookRepository.findById(id).getOrElse { throw NotFoundException() }

        return ReadWorkbookDetailResponse(
            workbook.id,
            workbook.title,
            workbook.description,
            workbook.emoji,
            workbook.quantity,
            workbook.modifiedAt,
        )
    }

    @Transactional(readOnly = true)
    fun readWorkbookList(memberId: UUID): List<ReadWorkbookListResponse> {
        val member = memberRepository.findById(memberId).getOrElse { throw InvalidInputException("memberId") }
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
        val workbook: Workbook =
            workbookRepository
                .findById(workbookId)
                .orElseThrow { NotFoundException(fieldName = "workbook", message = "id 를 재확인해주세요.") }
        var nextSequence = questionSetRepository.findMaxSequenceByWorkbookId(workbook) + 1

        request.questions.forEach { questionId ->
            val question: Question =
                questionRepository
                    .findById(questionId)
                    .getOrElse { throw NotFoundException(fieldName = "question", message = "id 를 재확인해주세요.") }

            if (questionSetRepository
                    .existsByQuestionIdAndWorkbookId(questionId, workbook.id)
            ) {
                throw AlreadyExistedException("questionId ($questionId)")
            }

            questionSetRepository.save(QuestionSet.createSequence(question, workbook, nextSequence))
            nextSequence += 1
        }

        workbook.increaseQuantity(request.questions.size)
    }

    @Transactional
    fun deleteQuestionInWorkbook(
        workbookId: UUID,
        questionId: UUID,
    ): DeleteQuestionInWorkbookResponse {
        val workbook =
            workbookRepository
                .findById(workbookId)
                .getOrElse { throw NotFoundException(fieldName = "workbook", message = "id 를 재확인해주세요.") }

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
        val workbook: Workbook =
            workbookRepository
                .findById(workbookId)
                .getOrElse { throw NotFoundException(fieldName = "workbook", message = "id를 재확인해주세요.") }

        // 질문 : 이런 로직도 비지니스 함수 내부로 넣어야할지
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
        val workbook: Workbook =
            workbookRepository
                .findById(workbookId)
                .getOrElse { throw NotFoundException(fieldName = "workbook", message = "id를 재확인해주세요.") }

        workbook.updateWorkbook(request.title, request.description)

        return UpdateWorkbookDetailResponse(workbookId)
    }

    fun readQuestionsInWorkbook(workbookId: UUID): List<ReadQuestionsInWorkbookResponse> {
        workbookRepository
            .findById(workbookId)
            .getOrElse { throw InvalidInputException(fieldName = "workbook", message = "id를 재확인해주세요.") }
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
}
