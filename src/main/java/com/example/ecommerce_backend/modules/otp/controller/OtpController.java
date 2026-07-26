package com.example.ecommerce_backend.modules.otp.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.otp.dto.request.SendOtpRequest;
import com.example.ecommerce_backend.modules.otp.dto.request.VerifyOtpRequest;
import com.example.ecommerce_backend.modules.otp.dto.response.OtpResponse;
import com.example.ecommerce_backend.modules.otp.mapper.OtpMapper;
import com.example.ecommerce_backend.modules.otp.service.OtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/otp")
@Tag(name = "OTP", description = "OTP API")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @PostMapping("/send")
    @Operation(summary = "Send OTP", description = "Send an OTP to the specified email or phone number")
    public ResponseEntity<ApiResponse<OtpResponse>> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        otpService.generateOtp(request.getEmailOrPhone());
        return ApiResponse.success(
                OtpMapper.toOtpResponse("OTP sent successfully"),
                "OTP sent successfully"
        );
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify OTP", description = "Verify the OTP code for the specified email or phone number")
    public ResponseEntity<ApiResponse<OtpResponse>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        boolean valid = otpService.validateOtp(request.getEmailOrPhone(), request.getOtp());
        if (valid) {
            otpService.invalidateOtp(request.getEmailOrPhone());
            return ApiResponse.success(
                    OtpMapper.toOtpResponse("OTP verified successfully"),
                    "OTP verified successfully"
            );
        }
        return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid or expired OTP");
    }
}
