package com.example.ecommerce_backend.modules.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Change password request")
public class ChangePasswordRequest {

    @NotBlank(message = "Email or phone is required")
    @Schema(description = "Email or phone number", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String emailOrPhone;

    @NotBlank(message = "OTP is required")
    @Schema(description = "OTP code", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String otp;

    @NotBlank(message = "Current password is required")
    @Schema(description = "Current password", example = "currentPass123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "New password must be at least 6 characters")
    @Schema(description = "New password (min 6 characters)", example = "newPass456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;
}
