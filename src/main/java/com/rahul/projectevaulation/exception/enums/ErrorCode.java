package com.rahul.projectevaulation.exception.enums;

/**
 * Application-level error codes returned to API clients.
 *
 * <p>Keep these stable once exposed to clients.
 */
public enum ErrorCode {
    USER_NOT_FOUND,
    INVALID_CREDENTIALS,
    VALIDATION_FAILED,
    ACCESS_DENIED,
    INTERNAL_ERROR,

    // --- New OTP & Messaging Error Codes ---
    OTP_INVALID,
    OTP_EXPIRED,
    OTP_MAX_ATTEMPTS,
    OTP_RESEND_COOLDOWN,
    MESSAGING_FAILURE
}
