package com.example.ecommerce_backend.modules.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Payment gateway request")
public class PaymentGatewayRequest {
    @NotBlank(message = "Code is required")
    @Schema(description = "Gateway code", example = "STRIPE", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @NotBlank(message = "Name is required")
    @Schema(description = "Gateway name", example = "Stripe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Gateway description", example = "Stripe payment gateway")
    private String description;
    @Schema(description = "Configuration template", example = "{\"apiKey\":\"...\"}")
    private String configTemplate;
}
