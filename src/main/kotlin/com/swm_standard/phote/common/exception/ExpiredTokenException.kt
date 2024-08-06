package com.swm_standard.phote.common.exception

class ExpiredTokenException(
    val fieldName: String = "",
    message: String = "유효하지 않는 token입니다.",
) : RuntimeException(message) {
    constructor(message: String) : this(message = message, fieldName = "")
}
