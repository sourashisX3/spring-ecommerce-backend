package com.example.ecommerce_backend.modules.shipping.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Shipping carrier request")
public class ShippingCarrierRequest {
    @NotBlank
    @Schema(description = "Carrier code", example = "UPS", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;
    @NotBlank
    @Schema(description = "Carrier name", example = "United Parcel Service", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @Schema(description = "Tracking URL template", example = "https://www.ups.com/track?num={trackingNumber}")
    private String trackingUrlTemplate;
}
