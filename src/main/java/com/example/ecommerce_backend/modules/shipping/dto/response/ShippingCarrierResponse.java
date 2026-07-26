package com.example.ecommerce_backend.modules.shipping.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Shipping carrier response")
public class ShippingCarrierResponse {
    @Schema(description = "Carrier ID", example = "1")
    private Long id;
    @Schema(description = "Carrier UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String uuid;
    @Schema(description = "Carrier code", example = "UPS")
    private String code;
    @Schema(description = "Carrier name", example = "United Parcel Service")
    private String name;
    @Schema(description = "Tracking URL template", example = "https://www.ups.com/track?num={trackingNumber}")
    private String trackingUrlTemplate;
    @JsonProperty("isActive")
    @Schema(description = "Whether the carrier is active", example = "true")
    private boolean isActive;
    @Schema(description = "Creation timestamp")
    private Instant createdAt;
    @Schema(description = "Last update timestamp")
    private Instant updatedAt;
}
