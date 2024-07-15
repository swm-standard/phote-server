package com.swm_standard.phote.service

import com.swm_standard.phote.common.exception.NotFoundException
import com.swm_standard.phote.dto.*
import com.swm_standard.phote.entity.Question
import com.swm_standard.phote.entity.Tag
import com.swm_standard.phote.repository.MemberRepository
import com.swm_standard.phote.repository.QuestionRepository
import com.swm_standard.phote.repository.TagRepository
import com.swm_standard.phote.repository.WorkbookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class QuestionService(
    private val questionRepository: QuestionRepository,
    private val memberRepository: MemberRepository,
    private val tagRepository: TagRepository,
    private val workbookRepository: WorkbookRepository
) {
    @Transactional
    fun createQuestion(memberId:UUID, request: CreateQuestionRequestDto, imageUrl: String?)
    : CreateQuestionResponseDto {

        // 문제 생성 유저 확인
        val member = memberRepository.findById(memberId).orElseThrow { NotFoundException("존재하지 않는 member") }

        // 문제 저장
        val question = questionRepository.save(
            Question(
                member = member,
                statement = request.statement,
                image = imageUrl,
                category = request.category,
                options = request.options,
                answer = request.answer,
                memo = request.memo
            )
        )

        // 태그 생성
        request.tags?.forEach {
            tagRepository.save(Tag(name = it, question = question))
        }

        return CreateQuestionResponseDto(question.id)
    }

    @Transactional(readOnly = true)
    fun readQuestionDetail(id: UUID): ReadQuestionDetailResponseDto {
        val question = questionRepository.findById(id).orElseThrow { NotFoundException("questionId","존재하지 않는 UUID") }

        return ReadQuestionDetailResponseDto(question)
    }

    @Transactional(readOnly = true)
    fun searchQuestions(memberId: UUID, tags: List<String>?, keywords: List<String>?): List<Question> {

        // 요청을 보낸 멤버가 생성한 문제이고, tags, keywords를 모두 포함하는 문제만 불러옴
        val questions: List<Question> = questionRepository.searchQuestionsList(memberId, tags, keywords)

        return questions
    }

    @Transactional
    fun deleteQuestion(id: UUID): DeleteQuestionResponseDto {

        // 존재하지 않는 question id가 아닌지 확인
        val question = questionRepository.findById(id).orElseThrow { NotFoundException("questionId","존재하지 않는 UUID") }

        // 연결된 workbook의 quantity 감소
        question.questionSet?.forEach { questionSet ->
            val workbook = questionSet.workbook
            workbook.decreaseQuantity()
            workbookRepository.save(workbook)
        }

        questionRepository.deleteById(id)

        return DeleteQuestionResponseDto(id, LocalDateTime.now())
    }
}