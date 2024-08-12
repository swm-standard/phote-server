package com.swm_standard.phote.controller

import com.swm_standard.phote.common.resolver.memberId.MemberId
import com.swm_standard.phote.common.responsebody.BaseResponse
import com.swm_standard.phote.dto.CreateQuestionRequest
import com.swm_standard.phote.dto.CreateQuestionResponse
import com.swm_standard.phote.dto.DeleteQuestionResponse
import com.swm_standard.phote.dto.ReadQuestionDetailResponse
import com.swm_standard.phote.dto.SearchQuestionsResponse
import com.swm_standard.phote.dto.SearchQuestionsToAddResponse
import com.swm_standard.phote.dto.TransformQuestionResponse
import com.swm_standard.phote.external.aws.S3Service
import com.swm_standard.phote.service.QuestionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/api")
@Tag(name = "Question", description = "Question API Document")
class QuestionController(
    private val questionService: QuestionService,
    private val s3Service: S3Service,
) {
    @Operation(summary = "createQuestion", description = "문제 생성")
    @SecurityRequirement(name = "bearer Auth")
    @PostMapping("/question")
    fun createQuestion(
        @Parameter(hidden = true) @MemberId memberId: UUID,
        @Valid @RequestBody request: CreateQuestionRequest,
    ): BaseResponse<CreateQuestionResponse> =
        BaseResponse(msg = "문제 생성 성공", data = questionService.createQuestion(memberId, request))

    @Operation(summary = "readQuestionDetail", description = "문제 상세 정보 조회")
    @SecurityRequirement(name = "bearer Auth")
    @GetMapping("/question/{id}")
    fun readQuestionDetail(
        @PathVariable(
            required = true,
        ) id: UUID,
    ): BaseResponse<ReadQuestionDetailResponse> =
        BaseResponse(msg = "문제 상세조회 성공", data = questionService.readQuestionDetail(id))

    @Operation(summary = "searchQuestions", description = "문제 검색")
    @SecurityRequirement(name = "bearer Auth")
    @GetMapping("/questions")
    fun searchQuestions(
        @Parameter(hidden = true) @MemberId memberId: UUID,
        @RequestParam(required = false) tags: List<String>? = null,
        @RequestParam(required = false) keywords: List<String>? = null,
    ): BaseResponse<List<SearchQuestionsResponse>> {
        questionService.searchQuestions(memberId, tags, keywords)
        return BaseResponse(msg = "문제 검색 성공", data = questionService.searchQuestions(memberId, tags, keywords))
    }

    @Operation(summary = "searchQuestionsToAdd", description = "문제집에 추가할 문제 검색")
    @SecurityRequirement(name = "bearer Auth")
    @GetMapping("/questions/workbook/{workbookId}")
    fun searchQuestionsToAdd(
        @Parameter(hidden = true) @MemberId memberId: UUID,
        @PathVariable(required = true) workbookId: UUID,
        @RequestParam(required = false) tags: List<String>? = null,
        @RequestParam(required = false) keywords: List<String>? = null,
    ): BaseResponse<List<SearchQuestionsToAddResponse>> =
        BaseResponse(
            msg = "문제집에 추가할 문제 목록 검색 성공",
            data = questionService.searchQuestionsToAdd(memberId, workbookId, tags, keywords),
        )

    @Operation(summary = "deleteQuestion", description = "문제 삭제")
    @SecurityRequirement(name = "bearer Auth")
    @DeleteMapping("/question/{id}")
    fun deleteQuestion(
        @PathVariable(required = true) id: UUID,
    ): BaseResponse<DeleteQuestionResponse> = BaseResponse(msg = "문제 삭제 성공", data = questionService.deleteQuestion(id))

    @Operation(summary = "transformQuestion", description = "문제 변환")
    @SecurityRequirement(name = "bearer Auth")
    @PostMapping("question-transform")
    fun transformQuestion(
        @RequestPart image: MultipartFile?,
        @RequestPart imageCoordinates: List<List<Int>>? = null,
    ): BaseResponse<TransformQuestionResponse> {
        val imageUrl = image?.let { s3Service.uploadChatGptImage(it) }
        val response: TransformQuestionResponse = questionService.transformQuestion(imageUrl!!, imageCoordinates)

        return BaseResponse(data = response, msg = "문제 변환 성공")
    }
}
