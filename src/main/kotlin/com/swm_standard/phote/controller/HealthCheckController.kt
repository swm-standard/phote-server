package com.swm_standard.phote.controller

import com.swm_standard.phote.common.responsebody.BaseResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthCheckController {
    @GetMapping("/")
    fun healthCheck(): BaseResponse<String> = BaseResponse(data = "정상 작동 중")
}
