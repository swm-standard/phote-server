package com.swm_standard.phote.common.exception

import com.swm_standard.phote.common.responsebody.BaseResponse
import com.swm_standard.phote.common.responsebody.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class CustomExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    protected fun methodArgumentNotValidException(
        ex: MethodArgumentNotValidException,
    ): ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mutableMapOf<String, String>()
        ex.bindingResult.allErrors.forEach { error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.defaultMessage
            errors[fieldName] = errorMessage ?: "Not Exception RequestMessage"
        }

        return ResponseEntity(
            BaseResponse(
                ErrorCode.ERROR.name,
                ErrorCode.BAD_REQUEST.statusCode,
                ErrorCode.BAD_REQUEST.msg,
                errors,
            ),
            HttpStatus.BAD_REQUEST,
        )
    }

    @ExceptionHandler(InvalidInputException::class)
    protected fun invalidInputException(ex: InvalidInputException): ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mapOf(ex.fieldName to (ex.message ?: "Not Exception RequestMessage"))
        return ResponseEntity(
            BaseResponse(
                ErrorCode.ERROR.name,
                ErrorCode.BAD_REQUEST.statusCode,
                ErrorCode.BAD_REQUEST.msg,
                errors,
            ),
            HttpStatus.BAD_REQUEST,
        )
    }

    @ExceptionHandler(Exception::class)
    protected fun defaultException(ex: Exception): ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mapOf("서버 내 오류" to (ex.message ?: "Not Exception RequestMessage"))
        return ResponseEntity(
            BaseResponse(
                ErrorCode.ERROR.name,
                ErrorCode.ERROR.statusCode,
                ErrorCode.ERROR.msg,
                errors,
            ),
            HttpStatus.INTERNAL_SERVER_ERROR,
        )
    }

    @ExceptionHandler(BadRequestException::class)
    protected fun badRequestException(ex: BadRequestException): ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mapOf(ex.fieldName to (ex.message ?: "Not Exception RequestMessage"))
        return ResponseEntity(
            BaseResponse(
                ErrorCode.ERROR.name,
                ErrorCode.BAD_REQUEST.statusCode,
                ex.message ?: ErrorCode.BAD_REQUEST.msg,
                errors,
            ),
            HttpStatus.BAD_REQUEST,
        )
    }

    @ExceptionHandler(NoResourceFoundException::class)
    protected fun noResourceException(ex: Exception): ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mapOf("id" to (ex.message ?: "Not Exception RequestMessage"))
        return ResponseEntity(
            BaseResponse(
                ErrorCode.ERROR.name,
                ErrorCode.BAD_REQUEST.statusCode,
                ErrorCode.BAD_REQUEST.msg,
                errors,
            ),
            HttpStatus.BAD_REQUEST,
        )
    }

    @ExceptionHandler(AlreadyExistedException::class)
    protected fun alreadyExistedException(
        ex: AlreadyExistedException,
    ): ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mapOf(ex.fieldName to (ex.message ?: "Not Exception RequestMessage"))
        return ResponseEntity(
            BaseResponse(
                ErrorCode.ERROR.name,
                ErrorCode.BAD_REQUEST.statusCode,
                ErrorCode.BAD_REQUEST.msg,
                errors,
            ),
            HttpStatus.BAD_REQUEST,
        )
    }

    @ExceptionHandler(ExpiredTokenException::class)
    protected fun expiredTokenException(ex: ExpiredTokenException): ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mapOf(ex.fieldName to (ex.message ?: "Not Exception RequestMessage"))
        return ResponseEntity(
            BaseResponse(
                ErrorCode.ERROR.name,
                ErrorCode.EXPIRED_TOKEN.statusCode,
                ErrorCode.EXPIRED_TOKEN.msg,
                errors,
            ),
            HttpStatus.UNAUTHORIZED,
        )
    }

    @ExceptionHandler(NotFoundException::class)
    protected fun notFoundException(ex: NotFoundException): ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mapOf(ex.fieldName to (ex.message ?: "Not Exception RequestMessage"))
        return ResponseEntity(
            BaseResponse(
                ErrorCode.ERROR.name,
                ErrorCode.NOT_FOUND.statusCode,
                ex.message ?: ErrorCode.NOT_FOUND.msg,
                errors,
            ),
            HttpStatus.NOT_FOUND,
        )
    }

    @ExceptionHandler(HandlerMethodValidationException::class)
    protected fun handlerMethodValidationException(
        ex: HandlerMethodValidationException,
    ): ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mutableMapOf<String, String>()

        ex.allErrors.forEach { error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.defaultMessage
            errors[fieldName] = errorMessage ?: "Not Exception RequestMessage"
        }

        return ResponseEntity(
            BaseResponse(ErrorCode.ERROR.name, ErrorCode.BAD_REQUEST.statusCode, ex.message, errors),
            HttpStatus.BAD_REQUEST,
        )
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    protected fun methodArgumentTypeMismatchException(
        ex: MethodArgumentTypeMismatchException,
    ): ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mapOf(ex.name to (ex.message ?: "Not Exception RequestMessage"))
        return ResponseEntity(
            BaseResponse(
                ErrorCode.ERROR.name,
                ErrorCode.BAD_REQUEST.statusCode,
                ErrorCode.BAD_REQUEST.msg,
                errors,
            ),
            HttpStatus.BAD_REQUEST,
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    protected fun httpMessageNotReadableException(
        ex: HttpMessageNotReadableException,
    ): ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mapOf("" to (ex.message ?: "Not Exception RequestMessage"))
        return ResponseEntity(
            BaseResponse(
                ErrorCode.ERROR.name,
                ErrorCode.BAD_REQUEST.statusCode,
                ErrorCode.BAD_REQUEST.msg,
                errors,
            ),
            HttpStatus.BAD_REQUEST,
        )
    }

    @ExceptionHandler(ChatGptErrorException::class)
    protected fun chatGptErrorException(ex: ChatGptErrorException): ResponseEntity<BaseResponse<Map<String, String>>> {
        val errors = mapOf(ex.fieldName to (ex.message ?: "Not Exception RequestMessage"))
        return ResponseEntity(
            BaseResponse(
                ErrorCode.ERROR.name,
                ErrorCode.BAD_GATEWAY.statusCode,
                ErrorCode.BAD_GATEWAY.msg,
                errors,
            ),
            HttpStatus.BAD_GATEWAY,
        )
    }
}
