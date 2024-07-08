package com.swm_standard.phote.controller

import com.swm_standard.phote.common.exception.InvalidInputException
import com.swm_standard.phote.common.responsebody.BaseResponse
import com.swm_standard.phote.dto.response.ReadQuestionDetailResponseDto
import com.swm_standard.phote.repository.QuestionRepository
import com.swm_standard.phote.service.QuestionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class QuestionController {
    @Autowired
    private lateinit var questionService: QuestionService

    @GetMapping("/api/question/{Id}")
    fun readQuestionDetail(@PathVariable(
        required = true
    ) Id: UUID
    ): BaseResponse<ReadQuestionDetailResponseDto> {
        return BaseResponse(msg = "문제 상세조회 성공", data = questionService.readQuestionDetail(Id))
    }
}