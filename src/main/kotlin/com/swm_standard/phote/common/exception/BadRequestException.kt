package com.swm_standard.phote.common.exception

class BadRequestException (
    val fieldName: String = "",
    message: String = "BadRequest"
): RuntimeException(message) {
    constructor(message: String) : this(message = message, fieldName = "")
}