package com.rahul.projectevaulation.projectinnovation.auth.dto;

import com.rahul.projectevaulation.shared.otp.enums.OtpPurpose;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VerifyOtpRequest {

    @NotBlank(message = "Recipient cannot be blank. Must be a valid email or phone number.")
    private String recipient;

    @NotBlank(message = "OTP cannot be blank.")
    private String otp;

    @NotNull(message = "Purpose cannot be null.")
    private OtpPurpose purpose;
}
