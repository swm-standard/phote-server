package com.swm_standard.phote.common.exception

class InvalidInputException(
    val fieldName: String = "",
    message: String = "Invalid Input"
) : RuntimeException(message)
