package com.swm_standard.phote.common.responsebody

import org.springframework.http.HttpStatus

enum class ResultCode(val statusCode: Int, val msg: String) {
    SUCCESS(HttpStatus.OK.value(),"정상 처리 되었습니다."),
    ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(),"에러가 발생했습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "요청 값이 올바르지 않습니다.")
}