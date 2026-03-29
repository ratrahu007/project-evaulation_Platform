package com.rahul.projectevaulation.shared.otp.dto;

import com.rahul.projectevaulation.shared.otp.enums.OtpChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendOtpResponse {
    private OtpChannel channel;
    private long resendCooldownSeconds;
}
