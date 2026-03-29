package com.rahul.projectevaulation.exception.custom;

import com.rahul.projectevaulation.exception.enums.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

/**
 * Base exception for application errors.
 *
 * <p>Services should throw this (or subclasses) to let the global handler return a
 * consistent error payload.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BaseException extends RuntimeException {

    private final ErrorCode errorCode;

    public BaseException(ErrorCode errorCode, String message) {
        super(Objects.requireNonNull(message, "message must not be null"));
        this.errorCode = Objects.requireNonNull(errorCode, "errorCode must not be null");
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
