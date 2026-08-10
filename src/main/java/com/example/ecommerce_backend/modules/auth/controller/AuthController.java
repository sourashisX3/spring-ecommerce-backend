package com.example.ecommerce_backend.modules.auth.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.auth.dto.request.LoginRequest;
import com.example.ecommerce_backend.modules.auth.dto.request.RefreshTokenRequest;
import com.example.ecommerce_backend.modules.auth.dto.request.RegisterRequest;
import com.example.ecommerce_backend.modules.auth.dto.request.ResetPasswordRequest;
import com.example.ecommerce_backend.modules.auth.dto.response.AuthResponse;
import com.example.ecommerce_backend.modules.otp.dto.request.SendOtpRequest;
import com.example.ecommerce_backend.modules.otp.dto.request.VerifyOtpRequest;
import com.example.ecommerce_backend.modules.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Auth", description = "Auth API")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Registers a new user with the provided registration details and returns authentication tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ApiResponse.created(response, "User registered successfully");
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates a user with email/phone and password, returning JWT tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ApiResponse.success(response, "Login successful");
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Refreshes the JWT access token using a valid refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refresh(request);
        return ApiResponse.success(response, "Token refreshed successfully");
    }

    @PostMapping("/send-otp")
    @Operation(summary = "Send OTP", description = "Sends a one-time password to the user's email or phone for verification")
    public ResponseEntity<ApiResponse<Map<String, String>>> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        String otp = authService.sendOtp(request);
        return ApiResponse.success(Map.of("otp", otp), "OTP sent successfully");
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP", description = "Verifies the one-time password and returns authentication tokens upon successful verification")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        AuthResponse response = authService.verifyOtp(request);
        return ApiResponse.success(response, "OTP verified successfully");
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Resets the user's password after verifying a valid OTP")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.success(null, "Password reset successfully");
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Logs out the authenticated user and invalidates their tokens")
    public ResponseEntity<ApiResponse<Void>> logout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            authService.logout(auth.getName());
        }
        return ApiResponse.success(null, "Logged out successfully");
    }
}
