package com.swm_standard.phote.controller

import com.swm_standard.phote.common.resolver.memberId.MemberId
import com.swm_standard.phote.common.responsebody.BaseResponse
import com.swm_standard.phote.dto.GradeExamRequest
import com.swm_standard.phote.dto.GradeExamResponse
import com.swm_standard.phote.dto.ReadExamHistoryDetailResponse
import com.swm_standard.phote.dto.ReadExamHistoryListResponse
import com.swm_standard.phote.service.ExamService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api")
@Tag(name = "Exam", description = "Exam API Document")
class ExamController(
    private val examService: ExamService,
) {
    @Operation(summary = "readExamHistoryDetail", description = "문제풀이 기록 상세조회")
    @SecurityRequirement(name = "bearer Auth")
    @GetMapping("/exam/{id}")
    fun readExamHistoryDetail(
        @PathVariable(
            required = true,
        ) id: UUID,
    ): BaseResponse<ReadExamHistoryDetailResponse> =
        BaseResponse(msg = "문제풀이 기록 상세조회 성공", data = examService.readExamHistoryDetail(id))

    @Operation(summary = "readExamHistoryList", description = "문제풀이 기록 리스트 조회")
    @SecurityRequirement(name = "bearer Auth")
    @GetMapping("/exams/{workbookId}")
    fun readExamHistoryList(
        @PathVariable(
            required = true,
        ) workbookId: UUID,
    ): BaseResponse<List<ReadExamHistoryListResponse>> =
        BaseResponse(msg = "문제풀이 기록 리스트 조회 성공", data = examService.readExamHistoryList(workbookId))

    @Operation(summary = "gradeExam", description = "문제풀이 제출 및 채점")
    @SecurityRequirement(name = "bearer Auth")
    @PostMapping("/exam/{workbookId}")
    fun gradeExam(
        @PathVariable(required = true) workbookId: UUID,
        @Valid @RequestBody request: List<GradeExamRequest>,
        @Parameter(hidden = true) @MemberId memberId: UUID,
    ): BaseResponse<GradeExamResponse> {
        val response = examService.gradeExam(workbookId, request, memberId)

        return BaseResponse(msg = "문제 풀이 채점 성공", data = response)
    }
}
