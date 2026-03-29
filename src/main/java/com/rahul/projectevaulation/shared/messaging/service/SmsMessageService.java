package com.rahul.projectevaulation.shared.messaging.service;

import com.rahul.projectevaulation.exception.enums.ErrorCode;
import com.rahul.projectevaulation.shared.messaging.config.TwilioConfig;
import com.rahul.projectevaulation.shared.messaging.dto.MessageRequest;
import com.rahul.projectevaulation.shared.otp.enums.OtpChannel;
import com.rahul.projectevaulation.shared.otp.exception.OtpException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmsMessageService implements MessageService {

    private final TwilioConfig twilioConfig;

    @Override
    public void send(MessageRequest request) {
        try {
            PhoneNumber to = new PhoneNumber(request.getRecipient());
            PhoneNumber from = new PhoneNumber(twilioConfig.getPhoneNumber());
            String body = request.getBody();

            Message.creator(to, from, body).create();
            log.info("SMS sent to {}", request.getRecipient());

        } catch (Exception e) {
            log.error("Failed to send SMS to {}", request.getRecipient(), e);
            throw new OtpException(ErrorCode.MESSAGING_FAILURE, "Failed to send SMS.");
        }
    }

    @Override
    public boolean supports(OtpChannel channel) {
        return channel == OtpChannel.SMS;
    }
}
