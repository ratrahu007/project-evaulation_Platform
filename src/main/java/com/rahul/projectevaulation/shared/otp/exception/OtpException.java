package com.rahul.projectevaulation.shared.otp.exception;

import com.rahul.projectevaulation.exception.custom.BaseException;
import com.rahul.projectevaulation.exception.enums.ErrorCode;

public class OtpException extends BaseException {
    public OtpException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
