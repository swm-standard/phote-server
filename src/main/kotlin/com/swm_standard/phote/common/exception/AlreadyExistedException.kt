package com.swm_standard.phote.common.exception

class AlreadyExistedException (
    val fieldName: String = "",
    message: String = "이미 존재하는 리소스입니다."
): RuntimeException(message)  