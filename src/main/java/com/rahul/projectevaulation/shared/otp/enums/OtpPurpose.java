package com.rahul.projectevaulation.shared.otp.enums;

/**
 * Defines the purpose of an OTP to prevent reuse across different workflows.
 */
public enum OtpPurpose {
    /**
     * For verifying a user's email address during registration.
     */
    EMAIL_VERIFICATION,

    /**
     * For resetting a forgotten password.
     */
    PASSWORD_RESET,

    /**
     * For two-factor authentication during login.
     */
    TWO_FACTOR_AUTH
}
