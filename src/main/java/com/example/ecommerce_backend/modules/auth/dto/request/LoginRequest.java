package com.example.ecommerce_backend.modules.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Login request")
public class LoginRequest {

    @NotBlank(message = "Email or phone is required")
    @Schema(description = "Email or phone number", example = "john@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String emailOrPhone;

    @NotBlank(message = "Password is required")
    @Schema(description = "Password", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
