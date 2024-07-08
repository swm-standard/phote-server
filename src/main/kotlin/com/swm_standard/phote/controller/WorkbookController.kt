package com.swm_standard.phote.controller

import com.swm_standard.phote.common.resolver.memberId.MemberId
import com.swm_standard.phote.common.responsebody.BaseResponse
import com.swm_standard.phote.dto.CreateWorkbookRequest
import com.swm_standard.phote.dto.CreateWorkbookResponse
import com.swm_standard.phote.service.WorkbookService
import jakarta.validation.Valid
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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

    @GetMapping("/test")
    fun test(@MemberId memberId: String) {
    }
}