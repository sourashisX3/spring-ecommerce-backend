package com.example.ecommerce_backend.modules.otp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendOtpRequest {

    @NotBlank(message = "Email or phone is required")
    private String emailOrPhone;
}
