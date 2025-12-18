package com.hipchapedia.presentation.api.advice

import com.hipchapedia.presentation.api.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 글로벌 예외 처리기
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    /**
     * Validation 에러 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors =
            ex.bindingResult.fieldErrors.associate {
                it.field to (it.defaultMessage ?: "유효하지 않은 값입니다.")
            }

        val errorResponse =
            ErrorResponse(
                message = "입력값 검증에 실패했습니다.",
                errors = errors,
            )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    /**
     * IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(message = ex.message ?: "잘못된 요청입니다.")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    /**
     * IllegalStateException 처리
     */
    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(ex: IllegalStateException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(message = ex.message ?: "처리 중 오류가 발생했습니다.")
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    /**
     * 일반 예외 처리
     */
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                message = "서버 오류가 발생했습니다: ${ex.message}",
            )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}
