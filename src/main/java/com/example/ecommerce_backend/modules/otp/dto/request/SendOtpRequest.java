package com.example.ecommerce_backend.modules.otp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Send OTP request")
public class SendOtpRequest {

    @NotBlank(message = "Email or phone is required")
    @Schema(description = "Email or phone number", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String emailOrPhone;
}
