package com.rahul.projectevaulation.shared.otp.service;

import com.rahul.projectevaulation.exception.enums.ErrorCode;
import com.rahul.projectevaulation.shared.messaging.service.EmailMessageService;
import com.rahul.projectevaulation.shared.messaging.service.MessageService;
import com.rahul.projectevaulation.shared.messaging.service.SmsMessageService;
import com.rahul.projectevaulation.shared.otp.config.OtpConfig;
import com.rahul.projectevaulation.shared.otp.dto.OtpVerificationResult;
import com.rahul.projectevaulation.shared.otp.dto.SendOtpResponse;
import com.rahul.projectevaulation.shared.otp.entity.Otp;
import com.rahul.projectevaulation.shared.otp.enums.OtpChannel;
import com.rahul.projectevaulation.shared.otp.enums.OtpPurpose;
import com.rahul.projectevaulation.shared.otp.exception.OtpException;
import com.rahul.projectevaulation.shared.otp.repository.OtpRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceImplTest {

    @Mock
    private OtpRepository otpRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private OtpConfig otpConfig;

    @Mock
    private EmailMessageService emailMessageService;

    @Mock
    private SmsMessageService smsMessageService;

    // The System Under Test
    private OtpServiceImpl otpService;

    private final String recipient = "test@example.com";
    private final OtpPurpose purpose = OtpPurpose.EMAIL_VERIFICATION;
    private final OtpChannel emailChannel = OtpChannel.EMAIL;
    private final OtpChannel smsChannel = OtpChannel.SMS;

    @BeforeEach
    void setUp() {
        // Manually construct the service with a list of mocked message services
        List<MessageService> messageServices = List.of(emailMessageService, smsMessageService);
        otpService = new OtpServiceImpl(otpRepository, messageServices, passwordEncoder, otpConfig);

        // Configure default OTP settings for all tests
        when(otpConfig.getExpiryMinutes()).thenReturn(5L);
        when(otpConfig.getLength()).thenReturn(6);
        when(otpConfig.getResendCooldown()).thenReturn(60L);
        when(otpConfig.getMaxAttempts()).thenReturn(5);
    }

    @Test
    void sendOtp_Success_ShouldUseEmailServiceForEmailChannel() {
        // Given
        when(otpRepository.findByRecipientAndPurposeAndActiveTrue(recipient, purpose)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedOtp");
        when(emailMessageService.supports(emailChannel)).thenReturn(true);

        // When
        SendOtpResponse response = otpService.sendOtp(recipient, purpose, emailChannel);

        // Then
        ArgumentCaptor<Otp> otpCaptor = ArgumentCaptor.forClass(Otp.class);
        verify(otpRepository).save(otpCaptor.capture());
        Otp savedOtp = otpCaptor.getValue();

        assertNotNull(response);
        assertEquals(emailChannel, response.getChannel());
        assertEquals(recipient, savedOtp.getRecipient());

        // Verify the correct message service was called
        verify(emailMessageService).send(any());
        verify(smsMessageService, never()).send(any());
    }

    @Test
    void sendOtp_Success_ShouldUseSmsServiceForSmsChannel() {
        // Given
        String phoneRecipient = "+15551234567";
        when(otpRepository.findByRecipientAndPurposeAndActiveTrue(phoneRecipient, purpose)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedOtp");
        when(smsMessageService.supports(smsChannel)).thenReturn(true);

        // When
        otpService.sendOtp(phoneRecipient, purpose, smsChannel);

        // Then
        // Verify the correct message service was called
        verify(smsMessageService).send(any());
        verify(emailMessageService, never()).send(any());
    }


    @Test
    void sendOtp_Failure_WhenResendCooldownIsActive() {
        // Given
        Otp existingOtp = new Otp();
        existingOtp.setCreatedAt(Instant.now().minusSeconds(30)); // Created 30 seconds ago
        when(otpRepository.findByRecipientAndPurposeAndActiveTrue(recipient, purpose)).thenReturn(Optional.of(existingOtp));

        // When & Then
        OtpException exception = assertThrows(OtpException.class, () -> otpService.sendOtp(recipient, purpose, emailChannel));

        assertEquals(ErrorCode.OTP_RESEND_COOLDOWN, exception.getErrorCode());
        verify(otpRepository, never()).save(any());
        verify(emailMessageService, never()).send(any());
    }

    @Test
    void verifyOtp_Success_ShouldInvalidateOtpAndReturnSuccess() {
        // Given
        String rawOtp = "123456";
        Otp storedOtp = new Otp();
        storedOtp.setRecipient(recipient);
        storedOtp.setPurpose(purpose);
        storedOtp.setHashedOtp("hashedOtp");
        storedOtp.setExpiresAt(Instant.now().plus(5, ChronoUnit.MINUTES));
        storedOtp.setActive(true);

        when(otpRepository.findByRecipientAndPurposeAndActiveTrue(recipient, purpose)).thenReturn(Optional.of(storedOtp));
        when(passwordEncoder.matches(rawOtp, "hashedOtp")).thenReturn(true);

        // When
        OtpVerificationResult result = otpService.verifyOtp(recipient, rawOtp, purpose);

        // Then
        assertTrue(result.isSuccess());
        ArgumentCaptor<Otp> otpCaptor = ArgumentCaptor.forClass(Otp.class);
        verify(otpRepository).save(otpCaptor.capture());
        assertFalse(otpCaptor.getValue().isActive());
        assertNotNull(otpCaptor.getValue().getVerifiedAt());
    }

    @Test
    void verifyOtp_Failure_WhenOtpIsInvalid() {
        // Given
        String rawOtp = "111111";
        Otp storedOtp = new Otp();
        storedOtp.setExpiresAt(Instant.now().plus(5, ChronoUnit.MINUTES));
        storedOtp.setHashedOtp("hashedOtp");

        when(otpRepository.findByRecipientAndPurposeAndActiveTrue(recipient, purpose)).thenReturn(Optional.of(storedOtp));
        when(passwordEncoder.matches(rawOtp, "hashedOtp")).thenReturn(false);

        // When & Then
        OtpException exception = assertThrows(OtpException.class, () -> otpService.verifyOtp(recipient, rawOtp, purpose));

        assertEquals(ErrorCode.OTP_INVALID, exception.getErrorCode());
        verify(otpRepository).save(any(Otp.class)); // To save the attempt count
    }

    @Test
    void verifyOtp_Failure_WhenOtpIsExpired() {
        // Given
        Otp storedOtp = new Otp();
        storedOtp.setExpiresAt(Instant.now().minus(1, ChronoUnit.MINUTES)); // Expired

        when(otpRepository.findByRecipientAndPurposeAndActiveTrue(recipient, purpose)).thenReturn(Optional.of(storedOtp));

        // When & Then
        OtpException exception = assertThrows(OtpException.class, () -> otpService.verifyOtp(recipient, "123456", purpose));

        assertEquals(ErrorCode.OTP_EXPIRED, exception.getErrorCode());
        ArgumentCaptor<Otp> otpCaptor = ArgumentCaptor.forClass(Otp.class);
        verify(otpRepository).save(otpCaptor.capture());
        assertFalse(otpCaptor.getValue().isActive());
    }

    @Test
    void verifyOtp_Failure_WhenMaxAttemptsReached() {
        // Given
        Otp storedOtp = new Otp();
        storedOtp.setExpiresAt(Instant.now().plus(5, ChronoUnit.MINUTES));
        storedOtp.setVerificationAttempts(5); // Max attempts reached

        when(otpRepository.findByRecipientAndPurposeAndActiveTrue(recipient, purpose)).thenReturn(Optional.of(storedOtp));

        // When & Then
        OtpException exception = assertThrows(OtpException.class, () -> otpService.verifyOtp(recipient, "123456", purpose));

        assertEquals(ErrorCode.OTP_MAX_ATTEMPTS, exception.getErrorCode());
        ArgumentCaptor<Otp> otpCaptor = ArgumentCaptor.forClass(Otp.class);
        verify(otpRepository).save(otpCaptor.capture());
        assertFalse(otpCaptor.getValue().isActive());
    }

    @Test
    void verifyOtp_Failure_WhenOtpNotFound() {
        // Given
        when(otpRepository.findByRecipientAndPurposeAndActiveTrue(recipient, purpose)).thenReturn(Optional.empty());

        // When & Then
        OtpException exception = assertThrows(OtpException.class, () -> otpService.verifyOtp(recipient, "123456", purpose));

        assertEquals(ErrorCode.OTP_INVALID, exception.getErrorCode());
    }
}
