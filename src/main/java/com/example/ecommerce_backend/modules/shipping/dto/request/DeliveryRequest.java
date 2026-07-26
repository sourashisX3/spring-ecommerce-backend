package com.example.ecommerce_backend.modules.shipping.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
@Schema(description = "Delivery request")
public class DeliveryRequest {

    @NotNull
    @Schema(description = "Shipping address ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long shippingAddressId;

    @NotBlank
    @Schema(description = "Carrier code", example = "UPS", requiredMode = Schema.RequiredMode.REQUIRED)
    private String carrierCode;

    @Schema(description = "Tracking number", example = "1Z999AA10123456784")
    private String trackingNumber;

    @Schema(description = "Estimated delivery date")
    private Instant estimatedDelivery;
}
