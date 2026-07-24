package com.example.ecommerce_backend.modules.role_user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendOtpRequest {

    @NotBlank(message = "Email or phone is required")
    private String emailOrPhone;
}
