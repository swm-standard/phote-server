package com.swm_standard.phote.service

import com.swm_standard.phote.common.exception.AlreadyExistedException
import com.swm_standard.phote.common.exception.InvalidInputException
import com.swm_standard.phote.common.exception.NotFoundException
import com.swm_standard.phote.dto.*
import com.swm_standard.phote.entity.Question
import com.swm_standard.phote.entity.QuestionSet
import com.swm_standard.phote.entity.Workbook
import com.swm_standard.phote.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class WorkbookService(
    private val workbookRepository: WorkbookRepository,
    private val memberRepository: MemberRepository,
    private val questionSetRepository: QuestionSetRepository,
    private val questionRepository: QuestionRepository,
) {

    @Transactional
    fun createWorkbook(title: String, description: String?, emoji: String, memberEmail: String): CreateWorkbookResponse {
        val member = memberRepository.findByEmail(memberEmail) ?: throw NotFoundException()
        val workbook = workbookRepository.save(Workbook(title, description, member, emoji))

        return CreateWorkbookResponse(workbook.id)
    }

    @Transactional
    fun deleteWorkbook(id: UUID): DeleteWorkbookResponse {

        workbookRepository.findById(id).orElseThrow { NotFoundException("workbookId","존재하지 않는 workbook") }

        workbookRepository.deleteById(id)

        return DeleteWorkbookResponse(id, LocalDateTime.now())
    }

    @Transactional(readOnly = true)
    fun readWorkbookDetail(id: UUID) : ReadWorkbookDetailResponse {
        val workbook = workbookRepository.findById(id).orElseThrow { NotFoundException() }


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
    fun readWorkbookList(memberId: UUID) : List<ReadWorkbookListResponse> {
        val member = memberRepository.findById(memberId).orElseThrow { InvalidInputException("memberId") }
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
    fun addQuestionsToWorkbook(workbookId: UUID, request: AddQuestionsToWorkbookRequest) {

        val workbook: Workbook = workbookRepository.findById(workbookId).orElseThrow { NotFoundException(fieldName = "workbook", message = "id 를 재확인해주세요.") }
        var nextSequence = questionSetRepository.findMaxSequenceByWorkbookId(workbook) + 1

        request.questions.forEach { questionId ->
            val question: Question = questionRepository.findById(questionId).orElseThrow { NotFoundException(fieldName = "question", message = "id 를 재확인해주세요.") }
            questionSetRepository.findByQuestionIdAndWorkbookId(questionId, workbook.id)?.let { throw  AlreadyExistedException("questionId ($questionId)") }
            questionSetRepository.save(QuestionSet(question, workbook, nextSequence))
            nextSequence += 1
        }

        workbook.increaseQuantity(request.questions.size)
    }

    @Transactional
    fun deleteQuestionInWorkbook(workbookId: UUID, questionId: UUID): DeleteQuestionInWorkbookResponse {
        val workbook = workbookRepository.findById(workbookId)
            .orElseThrow { NotFoundException(fieldName = "workbook", message = "id 를 재확인해주세요.") }

        questionSetRepository.findByQuestionIdAndWorkbookId(questionId, workbookId)?.also {
            questionSetRepository.delete(it)

            workbook.decreaseQuantity()

            return DeleteQuestionInWorkbookResponse(workbookId, questionId, LocalDateTime.now())
        }

        throw InvalidInputException("question", message = "문제집에 속해있지 않습니다.")
    }

    @Transactional
    fun updateQuestionSequence(workbookId: UUID, request: List<UpdateQuestionSequenceRequest>): UUID {
        val workbook: Workbook = workbookRepository.findById(workbookId)
            .orElseThrow { NotFoundException(fieldName = "workbook", message = "id를 재확인해주세요.") }

        if (!workbook.compareQuestionQuantity(request.size)) throw InvalidInputException(fieldName = "question", message = "문제집 내 모든 문제를 포함해주세요.")

        request.forEach {
            questionSetRepository.findById(it.id).orElseThrow { InvalidInputException("questionSet") }
                .apply {
                    updateSequence(it.sequence)
                }
        }

        return workbookId
    }

    fun readQuestionsInWorkbook(workbookId: UUID): List<ReadQuestionsInWorkbookResponse> {

        workbookRepository.findById(workbookId).orElseThrow{ InvalidInputException(fieldName = "workboook", message = "id를 재확인해주세요.")}
        val questionSets: List<QuestionSet> = questionSetRepository.findByWorkbookIdOrderBySequence(workbookId)

        return questionSets.map { set ->
            ReadQuestionsInWorkbookResponse(
                set.id,
                set.question.id,
                set.question.statement,
                set.question.options,
                set.question.image,
                set.question.category,
                set.sequence,
                set.question.tags
            )
        }
    }
}