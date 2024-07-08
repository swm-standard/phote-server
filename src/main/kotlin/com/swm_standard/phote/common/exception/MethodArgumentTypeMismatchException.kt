package com.swm_standard.phote.common.exception

class MethodArgumentTypeMismatchException (
    val fieldName: String = "",
    message: String = "Method Argument Type Mismatch Exception"
): RuntimeException(message) {
    constructor(message: String) : this(message = message, fieldName = "")
}