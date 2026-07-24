package com.example.ecommerce_backend.modules.role_user.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.exception.BaseException;
import com.example.ecommerce_backend.modules.role_user.dto.request.LoginRequest;
import com.example.ecommerce_backend.modules.role_user.dto.request.RefreshTokenRequest;
import com.example.ecommerce_backend.modules.role_user.dto.request.RegisterRequest;
import com.example.ecommerce_backend.modules.role_user.dto.request.SendOtpRequest;
import com.example.ecommerce_backend.modules.role_user.dto.request.VerifyOtpRequest;
import com.example.ecommerce_backend.modules.role_user.dto.response.AuthResponse;
import com.example.ecommerce_backend.modules.role_user.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ApiResponse.created(response, "User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ApiResponse.success(response, "Login successful");
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refresh(request);
        return ApiResponse.success(response, "Token refreshed successfully");
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<Map<String, String>>> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        String otp = authService.sendOtp(request);
        return ApiResponse.success(Map.of("otp", otp), "OTP sent successfully");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        AuthResponse response = authService.verifyOtp(request);
        return ApiResponse.success(response, "OTP verified successfully");
    }
}
