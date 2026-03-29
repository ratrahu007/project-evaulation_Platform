package com.rahul.projectevaulation.shared.messaging.service;

import com.rahul.projectevaulation.exception.enums.ErrorCode;
import com.rahul.projectevaulation.shared.messaging.dto.MessageRequest;
import com.rahul.projectevaulation.shared.otp.enums.OtpChannel;
import com.rahul.projectevaulation.shared.otp.exception.OtpException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailMessageService implements MessageService {

    private final JavaMailSender mailSender;

    @Override
    public void send(MessageRequest request) {
        if (!supports(request.getChannel())) {
            return; // Do nothing if the channel is not EMAIL
        }
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mailSender.createMimeMessage(), true);
            helper.setTo(request.getRecipient());
            helper.setSubject(request.getSubject());
            helper.setText(request.getBody(), true); // Set to true for HTML content
            mailSender.send(helper.getMimeMessage());
            log.info("Email sent to {}", request.getRecipient());
        } catch (Exception e) {
            log.error("Failed to send email to {}", request.getRecipient(), e);
            throw new OtpException(ErrorCode.MESSAGING_FAILURE, "Failed to send email.");
        }
    }

    @Override
    public boolean supports(OtpChannel channel) {
        return channel == OtpChannel.EMAIL;
    }
}
