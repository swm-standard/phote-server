package com.swm_standard.phote.common.exception

class ChatGptErrorException(
    val fieldName: String = "",
    message: String = "ChatGPT 실행 오류 발생",
) : RuntimeException(message) {
    constructor(message: String) : this(message = message, fieldName = "")
}
