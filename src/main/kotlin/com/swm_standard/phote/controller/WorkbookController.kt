package com.swm_standard.phote.controller

import com.swm_standard.phote.common.exception.InvalidInputException
import com.swm_standard.phote.common.resolver.memberId.MemberId
import com.swm_standard.phote.common.responsebody.BaseResponse
import com.swm_standard.phote.dto.*
import com.swm_standard.phote.service.WorkbookService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api")
@Tag(name = "Workbook", description = "Workbook API Document")
class WorkbookController(
    private val workbookService: WorkbookService,
) {
    @Operation(summary = "createWorkbook", description = "문제집 생성")
    @SecurityRequirement(name = "bearer Auth")
    @PostMapping("/workbook")
    fun createWorkbook(
        @Valid @RequestBody request: CreateWorkbookRequest,
        @Parameter(hidden = true) @MemberId memberId: UUID,
    ): BaseResponse<CreateWorkbookResponse> {
        val workbook = workbookService.createWorkbook(request, memberId)

        return BaseResponse(msg = "문제집 생성 성공", data = workbook)
    }

    @Operation(summary = "deleteWorkbook", description = "문제집 삭제")
    @SecurityRequirement(name = "bearer Auth")
    @DeleteMapping("/workbook/{workbookId}")
    fun deleteWorkbook(
        @PathVariable(required = true) workbookId: UUID,
    ): BaseResponse<DeleteWorkbookResponse> {
        val deletedWorkbook = workbookService.deleteWorkbook(workbookId)

        return BaseResponse(msg = "문제집 삭제 성공", data = deletedWorkbook)
    }

    @Operation(summary = "readWorkbookDetail", description = "문제집 정보 상세 조회")
    @SecurityRequirement(name = "bearer Auth")
    @GetMapping("/workbook/{workbookId}")
    fun readWorkbookDetail(
        @PathVariable(required = true) workbookId: UUID,
    ): BaseResponse<ReadWorkbookDetailResponse> {
        val workbookDetail = workbookService.readWorkbookDetail(workbookId)

        return BaseResponse(msg = "문제집 정보 읽기 성공", data = workbookDetail)
    }

    @Operation(summary = "readWorkbookList", description = "문제집 목록 조회")
    @SecurityRequirement(name = "bearer Auth")
    @GetMapping("/workbooks")
    fun readWorkbookList(
        @Parameter(hidden = true) @MemberId memberId: UUID,
    ): BaseResponse<List<ReadWorkbookListResponse>> {
        val readWorkbookList = workbookService.readWorkbookList(memberId)

        return BaseResponse(msg = "문제집 목록 조회 성공", data = readWorkbookList)
    }

    @Operation(summary = "addQuestionsToWorkbook", description = "문제집에 문제 추가")
    @SecurityRequirement(name = "bearer Auth")
    @PostMapping("/workbook/{workbookId}")
    fun addQuestionsToWorkbook(
        @PathVariable(required = true) workbookId: UUID,
        @RequestBody @Valid request: AddQuestionsToWorkbookRequest,
    ): BaseResponse<Unit> {
        if (request.questions.isEmpty()) {
            throw InvalidInputException(
                fieldName = "questions",
                message = "question을 담아 요청해주세요.",
            )
        }
        workbookService.addQuestionsToWorkbook(workbookId, request)

        return BaseResponse(msg = "문제집에 문제 추가 성공")
    }

    @Operation(summary = "deleteQuestionInWorkbook", description = "문제집 내 문제 삭제")
    @SecurityRequirement(name = "bearer Auth")
    @DeleteMapping("/workbook/{workbookId}/question/{questionId}")
    fun deleteQuestionInWorkbook(
        @PathVariable(required = true) workbookId: UUID,
        @PathVariable(required = true) questionId: UUID,
    ): BaseResponse<DeleteQuestionInWorkbookResponse> =
        BaseResponse(
            msg = "문제집의 문제 삭제 성공",
            data = workbookService.deleteQuestionInWorkbook(workbookId, questionId),
        )

    @Operation(summary = "updateQuestionSequence", description = "문제집 내 문제 순서 변경")
    @SecurityRequirement(name = "bearer Auth")
    @PatchMapping("/workbook/question-sequence/{workbookId}")
    fun updateQuestionSequence(
        @PathVariable(required = true) workbookId: UUID,
        @RequestBody @Valid request: List<UpdateQuestionSequenceRequest>,
    ): BaseResponse<UpdateQuestionSequenceResponse> {
        val response = UpdateQuestionSequenceResponse(workbookService.updateQuestionSequence(workbookId, request))

        return BaseResponse(msg = "문제집의 문제 순서 변경 성공", data = response)
    }

    @Operation(summary = "updateWorkbookDetail", description = "문제집 상제 정보 변경")
    @SecurityRequirement(name = "bearer Auth")
    @PutMapping("/workbook/{workbookId}")
    fun updateWorkbookDetail(
        @PathVariable(required = true) workbookId: UUID,
        @RequestBody @Valid request: UpdateWorkbookDetailRequest,
    ): BaseResponse<UpdateWorkbookDetailResponse> =
        BaseResponse(msg = "문제집의 상세정보 수정 성공", data = workbookService.updateWorkbookDetail(workbookId, request))

    @Operation(summary = "readQuestionsInWorkbook", description = "문제집 내 문제 목록 조회")
    @SecurityRequirement(name = "bearer Auth")
    @GetMapping("/workbook/questions/{workbookId}")
    fun readQuestionsInWorkbook(
        @PathVariable(required = true) workbookId: UUID,
    ): BaseResponse<List<ReadQuestionsInWorkbookResponse>> {
        val response = workbookService.readQuestionsInWorkbook(workbookId)

        return BaseResponse(msg = "문제집의 문제 목록 조회 성공", data = response)
    }

    @Operation(summary = "receiveSharedWorkbook", description = "공유받은 문제집 저장")
    @SecurityRequirement(name = "bearer Auth")
    @PostMapping("/shared-workbook")
    fun receiveSharedWorkbook(
        @Valid @RequestBody receiveSharedWorkbookRequest: ReceiveSharedWorkbookRequest,
        @MemberId memberId: UUID,
    ): BaseResponse<ReceiveSharedWorkbookResponse> {
        val response = workbookService.receiveSharedWorkbook(receiveSharedWorkbookRequest, memberId)

        return BaseResponse(data = response, msg = "문제집 공유 받기 성공")
    }
}
