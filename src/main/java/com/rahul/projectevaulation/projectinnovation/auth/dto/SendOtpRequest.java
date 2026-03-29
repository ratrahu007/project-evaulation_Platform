package com.rahul.projectevaulation.projectinnovation.auth.dto;

import com.rahul.projectevaulation.shared.otp.enums.OtpChannel;
import com.rahul.projectevaulation.shared.otp.enums.OtpPurpose;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendOtpRequest {

    @NotBlank(message = "Recipient cannot be blank. Must be a valid email or phone number.")
    private String recipient;

    @NotNull(message = "Channel cannot be null. Must be EMAIL or SMS.")
    private OtpChannel channel;

    @NotNull(message = "Purpose cannot be null.")
    private OtpPurpose purpose;
}
