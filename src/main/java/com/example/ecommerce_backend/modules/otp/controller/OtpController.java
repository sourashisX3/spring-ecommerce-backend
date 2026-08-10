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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/otp")
@Tag(name = "OTP", description = "OTP API")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @Value("${otp.expose-in-response:false}")
    private boolean exposeOtpInResponse;

    @PostMapping("/send")
    @Operation(summary = "Send OTP", description = "Send an OTP to the specified email or phone number")
    public ResponseEntity<ApiResponse<OtpResponse>> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        String otp = otpService.generateOtp(request.getEmailOrPhone());
        OtpResponse response = exposeOtpInResponse
                ? OtpMapper.toOtpResponse("OTP sent successfully", otp)
                : OtpMapper.toOtpResponse("OTP sent successfully");
        return ApiResponse.success(response, "OTP sent successfully");
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify OTP", description = "Verify the OTP code for the specified email or phone number")
    public ResponseEntity<ApiResponse<OtpResponse>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        boolean valid = otpService.validateOtp(request.getEmailOrPhone(), request.getOtp());
        if (valid) {
            return ApiResponse.success(
                    OtpMapper.toOtpResponse("OTP verified successfully"),
                    "OTP verified successfully"
            );
        }
        return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid or expired OTP");
    }
}
