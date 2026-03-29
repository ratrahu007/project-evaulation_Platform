package com.rahul.projectevaulation.shared.otp.entity;

import com.rahul.projectevaulation.shared.otp.enums.OtpChannel;
import com.rahul.projectevaulation.shared.otp.enums.OtpPurpose;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String recipient; // e.g., email or phone number

    @Column(nullable = false)
    private String hashedOtp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtpPurpose purpose;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtpChannel channel;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    private Instant verifiedAt;

    @Column(nullable = false)
    private int verificationAttempts = 0;

    @Column(nullable = false)
    private boolean active = true;
}
