package com.example.ecommerce_backend.modules.otp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "OTP response")
public class OtpResponse {

    @Schema(description = "Response message", example = "OTP sent successfully")
    private String message;
}
