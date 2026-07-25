package com.example.ecommerce_backend.modules.auth.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.auth.dto.request.LoginRequest;
import com.example.ecommerce_backend.modules.auth.dto.request.RefreshTokenRequest;
import com.example.ecommerce_backend.modules.auth.dto.request.RegisterRequest;
import com.example.ecommerce_backend.modules.auth.dto.response.AuthResponse;
import com.example.ecommerce_backend.modules.otp.dto.request.SendOtpRequest;
import com.example.ecommerce_backend.modules.otp.dto.request.VerifyOtpRequest;
import com.example.ecommerce_backend.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

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

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            authService.logout(auth.getName());
        }
        return ApiResponse.success(null, "Logged out successfully");
    }
}
