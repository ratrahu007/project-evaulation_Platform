package com.rahul.projectevaulation.shared.otp.service;

import com.rahul.projectevaulation.shared.otp.dto.OtpVerificationResult;
import com.rahul.projectevaulation.shared.otp.dto.SendOtpResponse;
import com.rahul.projectevaulation.shared.otp.enums.OtpChannel;
import com.rahul.projectevaulation.shared.otp.enums.OtpPurpose;

public interface OtpService {
    SendOtpResponse sendOtp(String recipient, OtpPurpose purpose, OtpChannel channel);
    OtpVerificationResult verifyOtp(String recipient, String submittedOtp, OtpPurpose purpose);
}
