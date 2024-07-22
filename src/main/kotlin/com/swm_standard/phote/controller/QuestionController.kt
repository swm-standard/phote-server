package com.swm_standard.phote.controller

import com.swm_standard.phote.common.resolver.memberId.MemberId
import com.swm_standard.phote.common.responsebody.BaseResponse
import com.swm_standard.phote.dto.*
import com.swm_standard.phote.entity.Question
import com.swm_standard.phote.external.aws.S3Service
import com.swm_standard.phote.service.QuestionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/api")
@Tag(name = "Question", description = "Question API Document")
class QuestionController(
    private val questionService: QuestionService,
    private val s3Service: S3Service
) {

    @Operation(summary = "createQuestion", description = "문제 생성")
    @SecurityRequirement(name = "bearer Auth")
    @PostMapping( "/question")
    fun createQuestion(@Parameter(hidden = true) @MemberId memberId: UUID,
                       @Valid @RequestPart request: CreateQuestionRequest,
                       @RequestPart image: MultipartFile?
    ): BaseResponse<CreateQuestionResponse> {
        val imageUrl = image?.let { s3Service.uploadImage(it) }
        return BaseResponse(msg = "문제 생성 성공", data = questionService.createQuestion(memberId, request, imageUrl))
    }

    @Operation(summary = "readQuestionDetail", description = "문제 상세 정보 조회")
    @SecurityRequirement(name = "bearer Auth")
    @GetMapping("/question/{id}")
    fun readQuestionDetail(@PathVariable(
        required = true
    ) id: UUID
    ): BaseResponse<ReadQuestionDetailResponse> {
        return BaseResponse(msg = "문제 상세조회 성공", data = questionService.readQuestionDetail(id))
    }

    @Operation(summary = "searchQuestions", description = "문제 검색")
    @SecurityRequirement(name = "bearer Auth")
    @GetMapping("/questions")
    fun searchQuestions(@Parameter(hidden = true) @MemberId memberId: UUID,
                        @RequestParam(required = false) tags: List<String>? = null,
                        @RequestParam(required = false) keywords: List<String>? = null): BaseResponse<List<Question>> {
        questionService.searchQuestions(memberId, tags, keywords)
        return BaseResponse(msg = "문제 검색 성공", data=questionService.searchQuestions(memberId, tags, keywords))
    }

    @Operation(summary = "searchQuestionsToAdd", description = "문제집에 추가할 문제 검색")
    @SecurityRequirement(name = "bearer Auth")
    @GetMapping("/questions/workbook/{workbookId}")
    fun searchQuestionsToAdd(@Parameter(hidden = true) @MemberId memberId: UUID,
                             @PathVariable(required = true) workbookId: UUID,
                             @RequestParam(required = false) tags: List<String>? = null,
                             @RequestParam(required = false) keywords: List<String>? = null): BaseResponse<List<SearchQuestionsToAddResponse>> {
        return BaseResponse(msg = "문제집에 추가할 문제 목록 검색 성공", data = questionService.searchQuestionsToAdd(memberId, workbookId, tags, keywords))
    }

    @Operation(summary = "deleteQuestion", description = "문제 삭제")
    @SecurityRequirement(name = "bearer Auth")
    @DeleteMapping("/question/{id}")
    fun deleteQuestion(@PathVariable(required = true) id: UUID)
    : BaseResponse<DeleteQuestionResponse>{
        return BaseResponse(msg = "문제 삭제 성공", data = questionService.deleteQuestion(id))
    }

}