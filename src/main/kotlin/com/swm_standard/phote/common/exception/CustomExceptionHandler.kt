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
        val errors = mapOf("서버 내 오류" to (ex.message ?: "Not Exception Message"))
        return ResponseEntity(BaseResponse(ResultCode.ERROR.name, ResultCode.ERROR.statusCode, ResultCode.ERROR.msg, errors), HttpStatus.INTERNAL_SERVER_ERROR)

    }

    @ExceptionHandler(NotFoundException::class)
    protected fun notFoundException(ex: NotFoundException) : ResponseEntity<BaseResponse<Map<String, String>>> {
        return ResponseEntity(BaseResponse(ResultCode.NOT_FOUND.name, ResultCode.NOT_FOUND.statusCode, ex.message ?: ResultCode.NOT_FOUND.msg), HttpStatus.NOT_FOUND)

    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    protected fun methodArgumentTypeMismatchException(ex: MethodArgumentTypeMismatchException) : ResponseEntity<BaseResponse<Map<String, String>>> {

        val errors = mapOf(ex.fieldName to (ex.message ?: "Not Exception Message"))
        return ResponseEntity(BaseResponse(ResultCode.BAD_REQUEST.name, ResultCode.BAD_REQUEST.statusCode, ex.message ?: ResultCode.BAD_REQUEST.msg, errors), HttpStatus.BAD_REQUEST)

    }
}