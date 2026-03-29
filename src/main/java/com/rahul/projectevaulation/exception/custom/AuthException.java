package com.rahul.projectevaulation.exception.custom;

import com.rahul.projectevaulation.exception.enums.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Authentication/Authorization failures.
 *
 * <p>Use {@link #invalidCredentials(String)} for bad credentials and
 * {@link #accessDenied(String)} when the authenticated user lacks permissions.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AuthException extends BaseException {

    public AuthException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static AuthException invalidCredentials(String message) {
        return new AuthException(ErrorCode.INVALID_CREDENTIALS, message);
    }

    public static AuthException accessDenied(String message) {
        return new AuthException(ErrorCode.ACCESS_DENIED, message);
    }
}
