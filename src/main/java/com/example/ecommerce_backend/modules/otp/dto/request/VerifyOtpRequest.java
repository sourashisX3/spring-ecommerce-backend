package com.example.ecommerce_backend.modules.otp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOtpRequest {

    @NotBlank(message = "Email or phone is required")
    private String emailOrPhone;

    @NotBlank(message = "OTP is required")
    private String otp;
}
