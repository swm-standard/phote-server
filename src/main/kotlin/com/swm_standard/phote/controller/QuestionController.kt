package com.swm_standard.phote.controller

import com.swm_standard.phote.common.exception.InvalidInputException
import com.swm_standard.phote.common.resolver.memberId.MemberId
import com.swm_standard.phote.common.responsebody.BaseResponse
import com.swm_standard.phote.dto.DeleteQuestionResponseDto
import com.swm_standard.phote.dto.ReadQuestionDetailResponseDto
import com.swm_standard.phote.service.QuestionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api")
class QuestionController {
    @Autowired
    private lateinit var questionService: QuestionService

    @GetMapping("/question/{id}")
    fun readQuestionDetail(@PathVariable(
        required = true
    ) id: UUID
    ): BaseResponse<ReadQuestionDetailResponseDto> {
        return BaseResponse(msg = "문제 상세조회 성공", data = questionService.readQuestionDetail(id))
    }

    @DeleteMapping("/question/{id}")
    fun deleteQuestion(@PathVariable(required = true) id: UUID)
    : BaseResponse<DeleteQuestionResponseDto>{
        return BaseResponse(msg = "문제 삭제 성공", data = questionService.deleteQuestion(id))
    }
}