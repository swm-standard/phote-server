package com.swm_standard.phote.controller

import com.swm_standard.phote.common.responsebody.BaseResponse
import com.swm_standard.phote.dto.ReadExamHistoryDetailResponse
import com.swm_standard.phote.dto.ReadExamHistoryListResponse
import com.swm_standard.phote.service.ExamService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.UUID

@RestController
@RequestMapping("/api")
@Tag(name = "Exam", description = "Exam API Document")
class ExamController(
    private val examService: ExamService
) {
    @Operation(summary = "readExamHistoryDetail", description = "문제풀이 기록 상세조회")
    @SecurityRequirement(name = "bearer Auth")
    @GetMapping("/exam/{id}")
    fun readExamHistoryDetail(
        @PathVariable(
            required = true
        ) id: UUID
    ): BaseResponse<ReadExamHistoryDetailResponse> =
        BaseResponse(msg = "문제풀이 기록 상세조회 성공", data = examService.readExamHistoryDetail(id))

    @Operation(summary = "readExamHistoryList", description = "문제풀이 기록 리스트 조회")
    @SecurityRequirement(name = "bearer Auth")
    @GetMapping("/exams/{workbookId}")
    fun readExamHistoryList(
        @PathVariable(
            required = true
        ) workbookId: UUID
    ): BaseResponse<List<ReadExamHistoryListResponse>> =
        BaseResponse(msg = "문제풀이 기록 리스트 조회 성공", data = examService.readExamHistoryList(workbookId))
}
