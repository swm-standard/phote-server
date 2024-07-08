package com.swm_standard.phote.common.exception

import com.swm_standard.phote.common.responsebody.BaseResponse
import com.swm_standard.phote.common.responsebody.ResultCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    protected fun methodArgumentNotValidException(ex: MethodArgumentNotValidException) : ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mutableMapOf<String, String>()
        ex.bindingResult.allErrors.forEach{ error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.defaultMessage
            errors[fieldName] = errorMessage ?: "Not Exception Message"
        }

        return ResponseEntity(BaseResponse(ResultCode.ERROR.name, ResultCode.ERROR.statusCode, ResultCode.ERROR.msg, errors), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidInputException::class)
    protected fun invalidInputException(ex: InvalidInputException) : ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mapOf(ex.fieldName to (ex.message ?: "Not Exception Message"))
        return ResponseEntity(BaseResponse(ResultCode.BAD_REQUEST.name, ResultCode.BAD_REQUEST.statusCode, ResultCode.BAD_REQUEST.msg, errors), HttpStatus.BAD_REQUEST)

    }

    @ExceptionHandler(Exception::class)
    protected fun defaultException(ex: Exception) : ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mapOf("미처리 에러" to (ex.message ?: "Not Exception Message"))
        return ResponseEntity(BaseResponse(ResultCode.ERROR.name, ResultCode.ERROR.statusCode, ResultCode.ERROR.msg, errors), HttpStatus.BAD_REQUEST)

    }

    @ExceptionHandler(NullPointerException::class)
    protected fun nullPointerException(ex: Exception) : ResponseEntity<BaseResponse<Map<String?, String>>> {
        val errors = mapOf(ex.message to (ex.message ?: "Not Exception Message"))
        return ResponseEntity(BaseResponse(ResultCode.ERROR.name, ResultCode.ERROR.statusCode, ResultCode.ERROR.msg, errors), HttpStatus.BAD_REQUEST)

    }
}