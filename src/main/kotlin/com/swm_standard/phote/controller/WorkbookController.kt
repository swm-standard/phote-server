package com.swm_standard.phote.controller

import com.swm_standard.phote.common.responsebody.BaseResponse
import com.swm_standard.phote.dto.DeleteWorkbookResponse
import com.swm_standard.phote.dto.CreateWorkbookRequest
import com.swm_standard.phote.dto.CreateWorkbookResponse
import com.swm_standard.phote.dto.ReadWorkbookDetailResponse
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

    @DeleteMapping("/workbook/{id}")
    fun deleteWorkbook(@PathVariable("id") id: UUID): BaseResponse<DeleteWorkbookResponse> {

        val deletedWorkbook = workbookService.deleteWorkbook(id)

        return BaseResponse(msg = "문제집 삭제 성공", data = deletedWorkbook)
    }

    @GetMapping("/workbook/{workbookId}")
    fun readWorkbookDetail(@Valid @PathVariable(required = true) workbookId: UUID): BaseResponse<ReadWorkbookDetailResponse> {

        val workbookDetail = workbookService.readWorkbookDetail(workbookId)

        return BaseResponse(msg = "문제집 정보 읽기 성공", data = workbookDetail)
    }
}