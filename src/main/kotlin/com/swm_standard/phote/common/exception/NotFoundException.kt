package com.swm_standard.phote.common.exception

class NotFoundException (
    val fieldName: String = "",
    message: String = "NotFound"
): RuntimeException(message) {
    constructor(message: String) : this(message = message, fieldName = "")
}