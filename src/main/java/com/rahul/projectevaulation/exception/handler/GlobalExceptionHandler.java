package com.rahul.projectevaulation.exception.handler;

import com.rahul.projectevaulation.exception.custom.BaseException;
import com.rahul.projectevaulation.exception.dto.ErrorResponse;
import com.rahul.projectevaulation.exception.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Centralized exception handling for REST controllers.
 *
 * <p>Converts internal exceptions into a consistent JSON API error payload.
 */

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle known application exceptions.
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex, HttpServletRequest request) {
        HttpStatus httpStatus = resolveHttpStatus(ex);
        String path = request.getRequestURI();

        ErrorResponse payload = new ErrorResponse(
                httpStatus.value(),
                ex.getMessage(),
                path,
                ex.getErrorCode()

        );

        // Don't log full stack traces for expected client errors.
        if (httpStatus.is5xxServerError()) {
            log.error("Unhandled application error. code={}, path={}", ex.getErrorCode(), path, ex);
        } else {
            log.warn("Handled application error. code={}, path={}, message={}",
                    ex.getErrorCode(), path, ex.getMessage());
        }

        return ResponseEntity.status(httpStatus).body(payload);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex,
                                                                  HttpServletRequest request) {
        String path = request.getRequestURI();
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Validation failed");

        ErrorResponse payload = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message,
                path,
                ErrorCode.VALIDATION_FAILED
        );

        log.warn("Validation error. path={}, message={}", path, message);
        return ResponseEntity.badRequest().body(payload);
    }

    /**
     * Fallback handler for all uncaught exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnhandledException(Exception ex, HttpServletRequest request) {
        String path = request.getRequestURI();

        // Always avoid leaking internal exception details to clients.
        ErrorResponse payload = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error",
                path,
                ErrorCode.INTERNAL_ERROR
        );

        log.error("Unhandled exception. path={}", path, ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payload);
    }

    private HttpStatus resolveHttpStatus(BaseException ex) {
        // Prefer mapping by ErrorCode for consistency.
        return switch (ex.getErrorCode()) {
            case USER_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case INVALID_CREDENTIALS -> HttpStatus.UNAUTHORIZED;
            case ACCESS_DENIED -> HttpStatus.FORBIDDEN;
            case VALIDATION_FAILED -> HttpStatus.BAD_REQUEST;
            case OTP_INVALID, OTP_EXPIRED -> HttpStatus.BAD_REQUEST;
            case OTP_MAX_ATTEMPTS, OTP_RESEND_COOLDOWN -> HttpStatus.TOO_MANY_REQUESTS;
            case MESSAGING_FAILURE, INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
