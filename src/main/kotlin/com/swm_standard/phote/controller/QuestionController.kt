package com.swm_standard.phote.controller

import com.swm_standard.phote.common.resolver.memberId.MemberId
import com.swm_standard.phote.common.responsebody.BaseResponse
import com.swm_standard.phote.dto.*
import com.swm_standard.phote.entity.Question
import com.swm_standard.phote.external.aws.S3Service
import com.swm_standard.phote.service.QuestionService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import org.springframework.web.filter.RequestContextFilter
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/api")
class QuestionController(
    private val questionService: QuestionService,
    private val s3Service: S3Service,
    private val requestContextFilter: RequestContextFilter
) {

    @PostMapping( "/question")
    fun createQuestion(@MemberId memberId: UUID,
                       @Valid @RequestPart request: CreateQuestionRequestDto,
                       @RequestPart image: MultipartFile?
    ): BaseResponse<CreateQuestionResponseDto> {
        val imageUrl = image?.let { s3Service.uploadImage(it) }
        return BaseResponse(msg = "문제 생성 성공", data = questionService.createQuestion(memberId, request, imageUrl))
    }

    @GetMapping("/question/{id}")
    fun readQuestionDetail(@PathVariable(
        required = true
    ) id: UUID
    ): BaseResponse<ReadQuestionDetailResponseDto> {
        return BaseResponse(msg = "문제 상세조회 성공", data = questionService.readQuestionDetail(id))
    }

    @GetMapping("/questions")
    fun searchQuestions(@MemberId memberId: UUID,
                        @RequestParam(required = false) tags: List<String>? = null,
                        @RequestParam(required = false) keywords: List<String>? = null): BaseResponse<List<Question>> {
        questionService.searchQuestions(memberId, tags, keywords)
        return BaseResponse(msg = "문제 검색 성공", data=questionService.searchQuestions(memberId, tags, keywords))
    }

    @DeleteMapping("/question/{id}")
    fun deleteQuestion(@PathVariable(required = true) id: UUID)
    : BaseResponse<DeleteQuestionResponseDto>{
        return BaseResponse(msg = "문제 삭제 성공", data = questionService.deleteQuestion(id))
    }

}