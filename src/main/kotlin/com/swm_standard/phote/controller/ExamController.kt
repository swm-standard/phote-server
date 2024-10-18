package com.swm_standard.phote.controller

import com.swm_standard.phote.common.resolver.memberId.MemberId
import com.swm_standard.phote.common.responsebody.BaseResponse
import com.swm_standard.phote.dto.CreateSharedExamRequest
import com.swm_standard.phote.dto.CreateSharedExamResponse
import com.swm_standard.phote.dto.GradeExamRequest
import com.swm_standard.phote.dto.GradeExamResponse
import com.swm_standard.phote.dto.ReadAllSharedExamsResponse
import com.swm_standard.phote.dto.ReadExamHistoryDetailResponse
import com.swm_standard.phote.dto.ReadExamHistoryListResponse
import com.swm_standard.phote.dto.ReadExamResultDetailResponse
import com.swm_standard.phote.dto.ReadExamResultsResponse
import com.swm_standard.phote.dto.RegradeExamRequest
import com.swm_standard.phote.dto.RegradeExamResponse
import com.swm_standard.phote.service.ExamService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
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

    @Operation(summary = "readExamResults", description = "(강사가) 학생들의 시험 결과 목록을 조회")
    @SecurityRequirement(name = "bearer Auth")
    @GetMapping("/exams/result/{examId}")
    fun readExamResults(
        @PathVariable(
            required = true,
        ) examId: UUID,
    ): BaseResponse<ReadExamResultsResponse> =
        BaseResponse(msg = "학생 시험 결과 조회 성공", data = examService.readExamResults(examId))

    @Operation(summary = "readExamResultDetail", description = "(강사가) 학생의 시험 결과 상세조회")
    @SecurityRequirement(name = "bearer Auth")
    @GetMapping("/exam/result/{examId}/{memberId}")
    fun readExamResultDetail(
        @PathVariable(
            required = true,
        ) examId: UUID,
        @PathVariable(
            required = true,
        ) memberId: UUID,
    ): BaseResponse<ReadExamResultDetailResponse> =
        BaseResponse(msg = "학생 시험 결과 상세조회 성공", data = examService.readExamResultDetail(examId, memberId))

    @Operation(summary = "gradeExam", description = "문제풀이 제출 및 채점")
    @SecurityRequirement(name = "bearer Auth")
    @PostMapping("/exam")
    fun gradeExam(
        @Valid @RequestBody request: GradeExamRequest,
        @Parameter(hidden = true) @MemberId memberId: UUID,
    ): BaseResponse<GradeExamResponse> {
        val response = examService.gradeExam(request, memberId)

        return BaseResponse(msg = "문제 풀이 채점 성공", data = response)
    }

    @Operation(summary = "regradeExam", description = "(강사가) 시험 정오답 재채점")
    @SecurityRequirement(name = "bearer Auth")
    @PatchMapping("/exam/{examId}/{memberId}")
    fun regradeExam(
        @PathVariable(required = true) examId: UUID,
        @PathVariable(required = true) memberId: UUID,
        @Valid @RequestBody request: RegradeExamRequest,
    ): BaseResponse<RegradeExamResponse> {
        val response = examService.regradeExam(examId, memberId, request)

        return BaseResponse(msg = "문제 재채점 성공", data = response)
    }

    @Operation(summary = "createSharedExam", description = "공유용 시험 생성")
    @SecurityRequirement(name = "bearer Auth")
    @PostMapping("/exam/create")
    fun createSharedExam(
        @Parameter(hidden = true) @MemberId memberId: UUID,
        @Valid @RequestBody request: CreateSharedExamRequest,
    ): BaseResponse<CreateSharedExamResponse> {
        val sharedExamId: UUID = examService.createSharedExam(memberId, request)

        return BaseResponse(data = CreateSharedExamResponse(sharedExamId), msg = "공유용 시험 생성 성공")
    }

    @Operation(summary = "readAllSharedExams", description = "공유용 시험 목록 조회")
    @SecurityRequirement(name = "bearer Auth")
    @GetMapping("/exams")
    fun readAllSharedExams(
        @Parameter(hidden = true) @MemberId memberId: UUID,
    ): BaseResponse<List<ReadAllSharedExamsResponse>> =
        BaseResponse(data = examService.readAllSharedExams(memberId), msg = "공유용 시험 목록 조회 성공")
}
