package com.swm_standard.phote.controller

import com.swm_standard.phote.common.responsebody.BaseResponse
import com.swm_standard.phote.dto.WorkbookCreationRequest
import com.swm_standard.phote.dto.WorkbookCreationResponse
import com.swm_standard.phote.entity.Workbook
import com.swm_standard.phote.service.WorkbookService
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api")
class WorkbookController(private val workbookService: WorkbookService) {

    @PostMapping("/workbook")
    fun createWorkbook(@RequestBody request: WorkbookCreationRequest, authentication: Authentication): BaseResponse<WorkbookCreationResponse> {

        val workbook = workbookService.createWorkbook(
            request.title,
            request.description,
            authentication.name
        )

        return BaseResponse(msg = "문제집 생성 성공", data = workbook)
    }
}