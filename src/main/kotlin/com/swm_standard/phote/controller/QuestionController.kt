package com.swm_standard.phote.controller

import com.swm_standard.phote.common.resolver.memberId.MemberId
import com.swm_standard.phote.common.responsebody.BaseResponse
import com.swm_standard.phote.dto.CreateQuestionRequestDto
import com.swm_standard.phote.dto.CreateQuestionResponseDto
import com.swm_standard.phote.dto.DeleteQuestionResponseDto
import com.swm_standard.phote.dto.ReadQuestionDetailResponseDto
import com.swm_standard.phote.external.aws.S3Service
import com.swm_standard.phote.service.QuestionService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/api")
class QuestionController(
    private val questionService: QuestionService,
    private val s3Service: S3Service
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

    @DeleteMapping("/question/{id}")
    fun deleteQuestion(@PathVariable(required = true) id: UUID)
    : BaseResponse<DeleteQuestionResponseDto>{
        return BaseResponse(msg = "문제 삭제 성공", data = questionService.deleteQuestion(id))
    }
}