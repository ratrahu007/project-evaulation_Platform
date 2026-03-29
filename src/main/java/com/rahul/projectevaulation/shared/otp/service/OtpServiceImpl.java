package com.rahul.projectevaulation.shared.otp.service;

import com.rahul.projectevaulation.exception.enums.ErrorCode;
import com.rahul.projectevaulation.shared.messaging.dto.MessageRequest;
import com.rahul.projectevaulation.shared.otp.config.OtpConfig;
import com.rahul.projectevaulation.shared.otp.dto.OtpVerificationResult;
import com.rahul.projectevaulation.shared.otp.dto.SendOtpResponse;
import com.rahul.projectevaulation.shared.otp.entity.Otp;
import com.rahul.projectevaulation.shared.otp.enums.OtpChannel;
import com.rahul.projectevaulation.shared.otp.enums.OtpPurpose;
import com.rahul.projectevaulation.shared.otp.exception.OtpException;
import com.rahul.projectevaulation.shared.otp.repository.OtpRepository;
import com.rahul.projectevaulation.shared.messaging.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final List<MessageService> messageServices;
    private final PasswordEncoder passwordEncoder;
    private final OtpConfig otpConfig;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public SendOtpResponse sendOtp(String recipient, OtpPurpose purpose, OtpChannel channel) {
        otpRepository.findByRecipientAndPurposeAndActiveTrue(recipient, purpose).ifPresent(otp -> {
            if (otp.getCreatedAt().plusSeconds(otpConfig.getResendCooldown()).isAfter(Instant.now())) {
                throw new OtpException(ErrorCode.OTP_RESEND_COOLDOWN, "Please wait before requesting a new OTP.");
            }
        });

        otpRepository.invalidateActiveOtps(recipient, purpose);

        String rawOtp = generateRandomOtp();
        String hashedOtp = passwordEncoder.encode(rawOtp);

        Otp otp = new Otp();
        otp.setRecipient(recipient);
        otp.setHashedOtp(hashedOtp);
        otp.setPurpose(purpose);
        otp.setChannel(channel);
        otp.setExpiresAt(Instant.now().plus(otpConfig.getExpiryMinutes(), ChronoUnit.MINUTES));
        otpRepository.save(otp);

        sendMessage(new MessageRequest(
                recipient,
                "Your OTP Code",
                "Your OTP code is: " + rawOtp,
                channel
        ));

        return new SendOtpResponse(channel, otpConfig.getResendCooldown());
    }

    @Override
    @Transactional
    public OtpVerificationResult verifyOtp(String recipient, String submittedOtp, OtpPurpose purpose) {
        Otp otp = otpRepository.findByRecipientAndPurposeAndActiveTrue(recipient, purpose)
                .orElseThrow(() -> new OtpException(ErrorCode.OTP_INVALID, "OTP not found or already used."));

        if (otp.getExpiresAt().isBefore(Instant.now())) {
            otp.setActive(false);
            otpRepository.save(otp);
            throw new OtpException(ErrorCode.OTP_EXPIRED, "OTP has expired.");
        }

        if (otp.getVerificationAttempts() >= otpConfig.getMaxAttempts()) {
            otp.setActive(false);
            otpRepository.save(otp);
            throw new OtpException(ErrorCode.OTP_MAX_ATTEMPTS, "Maximum verification attempts reached.");
        }

        otp.setVerificationAttempts(otp.getVerificationAttempts() + 1);

        if (!passwordEncoder.matches(submittedOtp, otp.getHashedOtp())) {
            otpRepository.save(otp);
            throw new OtpException(ErrorCode.OTP_INVALID, "Invalid OTP code.");
        }

        otp.setActive(false);
        otp.setVerifiedAt(Instant.now());
        otpRepository.save(otp);

        return new OtpVerificationResult(true, "OTP verified successfully.");
    }

    private String generateRandomOtp() {
        return IntStream.range(0, otpConfig.getLength())
                .map(i -> secureRandom.nextInt(10))
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
    }

    private void sendMessage(MessageRequest request) {
        messageServices.stream()
                .filter(s -> s.supports(request.getChannel()))
                .findFirst()
                .orElseThrow(() -> new OtpException(ErrorCode.MESSAGING_FAILURE, "No message service configured for channel: " + request.getChannel()))
                .send(request);
    }
}
