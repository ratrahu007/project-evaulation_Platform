package com.rahul.projectevaulation.shared.messaging.service;

import com.rahul.projectevaulation.shared.messaging.dto.MessageRequest;
import com.rahul.projectevaulation.shared.otp.enums.OtpChannel;

public interface MessageService {
    void send(MessageRequest request);

    boolean supports(OtpChannel channel);
}
