package com.rahul.projectevaulation.shared.otp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerificationResult {
    private boolean success;
    private String message;
}
