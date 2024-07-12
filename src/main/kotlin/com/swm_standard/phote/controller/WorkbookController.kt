package com.swm_standard.phote.controller

import com.swm_standard.phote.common.exception.InvalidInputException
import com.swm_standard.phote.common.resolver.memberId.MemberId
import com.swm_standard.phote.common.responsebody.BaseResponse
import com.swm_standard.phote.dto.*
import com.swm_standard.phote.service.WorkbookService
import jakarta.validation.Valid
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api")
class WorkbookController(private val workbookService: WorkbookService) {

    @PostMapping("/workbook")
    fun createWorkbook(@Valid @RequestBody request: CreateWorkbookRequest, authentication: Authentication): BaseResponse<CreateWorkbookResponse> {

        val workbook = workbookService.createWorkbook(
            request.title,
            request.description,
            request.emoji,
            authentication.name
        )

        return BaseResponse(msg = "문제집 생성 성공", data = workbook)
    }

    @DeleteMapping("/workbook/{workbookId}")
    fun deleteWorkbook(@PathVariable workbookId: UUID): BaseResponse<DeleteWorkbookResponse> {

        val deletedWorkbook = workbookService.deleteWorkbook(workbookId)

        return BaseResponse(msg = "문제집 삭제 성공", data = deletedWorkbook)
    }

    @GetMapping("/workbook/{workbookId}")
    fun readWorkbookDetail(@Valid @PathVariable(required = true) workbookId: UUID): BaseResponse<ReadWorkbookDetailResponse> {

        val workbookDetail = workbookService.readWorkbookDetail(workbookId)

        return BaseResponse(msg = "문제집 정보 읽기 성공", data = workbookDetail)
    }

    @GetMapping("/workbooks")
    fun readWorkbookList(@MemberId memberId: UUID): BaseResponse<List<ReadWorkbookListResponse>> {

        val readWorkbookList = workbookService.readWorkbookList(memberId)

        return BaseResponse(msg = "문제집 목록 조회 성공", data = readWorkbookList)
    }

    @PostMapping("/workbook/{workbookId}")
    fun addQuestionstoWorkbook(@PathVariable workbookId: UUID, @RequestBody @Valid request: AddQuestionstoWorkbookRequest): BaseResponse<Unit> {
        if(request.questions.isEmpty()) throw InvalidInputException(fieldName = "questions", message = "question을 담아 요청해주세요.")
        workbookService.addQuestionstoWorkbook(workbookId,request)

        return BaseResponse(msg = "문제집에 문제 추가 성공")

    }
}