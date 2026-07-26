package com.example.ecommerce_backend.modules.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payment gateway response")
public class PaymentGatewayResponse {
    @Schema(description = "Gateway ID", example = "1")
    private Long id;
    @Schema(description = "Gateway UUID", example = "gateway-uuid-123")
    private String uuid;
    @Schema(description = "Gateway code", example = "STRIPE")
    private String code;
    @Schema(description = "Gateway name", example = "Stripe")
    private String name;
    @Schema(description = "Gateway description", example = "Stripe payment gateway")
    private String description;
    @Schema(description = "Configuration template", example = "{\"apiKey\":\"...\"}")
    private String configTemplate;
    @Schema(description = "Whether gateway is active", example = "true")
    private boolean isActive;
    @Schema(description = "Creation timestamp")
    private Instant createdAt;
    @Schema(description = "Update timestamp")
    private Instant updatedAt;

    @JsonProperty("isActive")
    public boolean isActive() { return isActive; }

    @JsonProperty("isActive")
    public void setActive(boolean isActive) { this.isActive = isActive; }
}
