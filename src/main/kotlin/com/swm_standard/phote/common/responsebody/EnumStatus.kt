package com.swm_standard.phote.common.responsebody

import org.springframework.http.HttpStatus

enum class ResultCode(val statusCode: Int, val msg: String) {
    SUCCESS(HttpStatus.OK.value(),"정상 처리 되었습니다."),
    ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(),"서버 내 오류"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "요청 값이 올바르지 않습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND.value(), "요청한 페이지를 찾을 수 없습니다.")
}