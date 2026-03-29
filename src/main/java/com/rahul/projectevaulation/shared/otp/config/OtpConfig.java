package com.rahul.projectevaulation.shared.otp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.otp")
@Data
public class OtpConfig {
    private int length = 6;
    private long expiryMinutes = 5;
    private int maxAttempts = 5;
    private long resendCooldown = 60;
}
