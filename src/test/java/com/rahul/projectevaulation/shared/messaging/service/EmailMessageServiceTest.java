package com.rahul.projectevaulation.shared.messaging.service;

import com.rahul.projectevaulation.shared.messaging.dto.MessageRequest;
import com.rahul.projectevaulation.shared.otp.enums.OtpChannel;
import com.rahul.projectevaulation.shared.otp.exception.OtpException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailMessageServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailMessageService emailMessageService;

    @Test
    void send_Success_WhenChannelIsEmail() {
        // Given
        MessageRequest request = new MessageRequest("test@example.com", "Test Subject", "Test Body", OtpChannel.EMAIL);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        emailMessageService.send(request);

        // Then
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void send_ShouldDoNothing_WhenChannelIsNotEmail() {
        // Given
        MessageRequest request = new MessageRequest("1234567890", "Test Subject", "Test Body", OtpChannel.SMS);

        // When
        emailMessageService.send(request);

        // Then
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void send_Failure_ShouldThrowOtpExceptionWhenMailFails() {
        // Given
        MessageRequest request = new MessageRequest("test@example.com", "Test Subject", "Test Body", OtpChannel.EMAIL);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Mail server is down")).when(mailSender).send(mimeMessage);

        // When & Then
        assertThrows(OtpException.class, () -> {
            emailMessageService.send(request);
        });
    }
}
