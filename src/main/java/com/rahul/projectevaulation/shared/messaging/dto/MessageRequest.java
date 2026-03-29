package com.rahul.projectevaulation.shared.messaging.dto;

import com.rahul.projectevaulation.shared.otp.enums.OtpChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {
    private String recipient;
    private String subject;
    private String body;
    private OtpChannel channel;
}
