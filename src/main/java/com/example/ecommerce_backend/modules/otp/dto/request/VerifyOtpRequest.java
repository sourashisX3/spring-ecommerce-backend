package com.example.ecommerce_backend.modules.otp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Verify OTP request")
public class VerifyOtpRequest {

    @NotBlank(message = "Email or phone is required")
    @Schema(description = "Email or phone number", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String emailOrPhone;

    @NotBlank(message = "OTP is required")
    @Schema(description = "OTP code", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String otp;
}
