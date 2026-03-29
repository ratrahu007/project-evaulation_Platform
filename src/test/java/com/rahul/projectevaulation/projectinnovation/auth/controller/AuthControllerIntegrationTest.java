package com.rahul.projectevaulation.projectinnovation.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahul.projectevaulation.projectinnovation.auth.dto.SendOtpRequest;
import com.rahul.projectevaulation.projectinnovation.auth.dto.VerifyOtpRequest;
import com.rahul.projectevaulation.shared.otp.dto.OtpVerificationResult;
import com.rahul.projectevaulation.shared.otp.dto.SendOtpResponse;
import com.rahul.projectevaulation.shared.otp.enums.OtpChannel;
import com.rahul.projectevaulation.shared.otp.enums.OtpPurpose;
import com.rahul.projectevaulation.shared.otp.service.OtpService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@WithMockUser
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OtpService otpService;

    @Test
    void sendOtp_Success_ShouldReturnOk() throws Exception {
        // Given
        SendOtpRequest request = new SendOtpRequest();
        request.setRecipient("test@example.com");
        request.setPurpose(OtpPurpose.EMAIL_VERIFICATION);
        request.setChannel(OtpChannel.EMAIL);

        SendOtpResponse serviceResponse = new SendOtpResponse(OtpChannel.EMAIL, 60L);
        when(otpService.sendOtp(anyString(), any(OtpPurpose.class), any(OtpChannel.class))).thenReturn(serviceResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/send-otp")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.channel").value("EMAIL"))
                .andExpect(jsonPath("$.resendCooldownSeconds").value(60));
    }

    @Test
    void sendOtp_Failure_WhenRecipientIsBlank() throws Exception {
        // Given
        SendOtpRequest request = new SendOtpRequest();
        request.setRecipient(""); // Blank recipient
        request.setPurpose(OtpPurpose.EMAIL_VERIFICATION);
        request.setChannel(OtpChannel.EMAIL);

        // When & Then
        mockMvc.perform(post("/api/auth/send-otp")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void verifyOtp_Success_ShouldReturnOk() throws Exception {
        // Given
        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setRecipient("test@example.com");
        request.setOtp("123456");
        request.setPurpose(OtpPurpose.EMAIL_VERIFICATION);

        OtpVerificationResult serviceResult = new OtpVerificationResult(true, "OTP verified successfully.");
        when(otpService.verifyOtp(anyString(), anyString(), any(OtpPurpose.class))).thenReturn(serviceResult);

        // When & Then
        mockMvc.perform(post("/api/auth/verify-otp")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("OTP verified successfully."));
    }

    @Test
    void verifyOtp_Failure_WhenOtpIsBlank() throws Exception {
        // Given
        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setRecipient("test@example.com");
        request.setOtp(""); // Blank OTP
        request.setPurpose(OtpPurpose.EMAIL_VERIFICATION);

        // When & Then
        mockMvc.perform(post("/api/auth/verify-otp")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
