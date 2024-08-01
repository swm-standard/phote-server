package com.swm_standard.phote.controller

import com.swm_standard.phote.common.responsebody.BaseResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class JenkinsTestController {
    @GetMapping("/test")
    fun test(): BaseResponse<Unit> = BaseResponse(msg = "this is a test.")
}
