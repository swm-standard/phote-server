package com.swm_standard.phote.common.exception

class AlreadyDeletedException (
    val fieldName: String = "",
    message: String = "이미 삭제된 리소스입니다."
): RuntimeException(message)  