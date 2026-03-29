package com.rahul.projectevaulation.projectinnovation.auth.controller;

import com.rahul.projectevaulation.projectinnovation.auth.dto.SendOtpRequest;
import com.rahul.projectevaulation.projectinnovation.auth.dto.VerifyOtpRequest;
import com.rahul.projectevaulation.shared.otp.dto.OtpVerificationResult;
import com.rahul.projectevaulation.shared.otp.dto.SendOtpResponse;
import com.rahul.projectevaulation.shared.otp.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OtpService otpService;
    // In a real app, you would inject your UserService here to activate the user, etc.
    // private final UserService userService;

    @PostMapping("/send-otp")
    public ResponseEntity<SendOtpResponse> sendOtp(@RequestBody @Valid SendOtpRequest request) {
        SendOtpResponse response = otpService.sendOtp(request.getRecipient(), request.getPurpose(), request.getChannel());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<OtpVerificationResult> verifyOtp(@RequestBody @Valid VerifyOtpRequest request) {
        OtpVerificationResult result = otpService.verifyOtp(request.getRecipient(), request.getOtp(), request.getPurpose());
        
        // Example of what you would do on success
        // if (result.isSuccess() && request.getPurpose() == OtpPurpose.EMAIL_VERIFICATION) {
        //     userService.activateUser(request.getRecipient());
        // }
        
        return ResponseEntity.ok(result);
    }
}
