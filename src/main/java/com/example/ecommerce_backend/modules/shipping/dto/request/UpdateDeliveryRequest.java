package com.example.ecommerce_backend.modules.shipping.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;

@Data
@Schema(description = "Update delivery request")
public class UpdateDeliveryRequest {

    @NotBlank(message = "Carrier code is required")
    @Schema(description = "Carrier code", example = "UPS", requiredMode = Schema.RequiredMode.REQUIRED)
    private String carrierCode;

    @Schema(description = "Tracking number", example = "1Z999AA10123456784")
    private String trackingNumber;

    @NotBlank(message = "Status is required")
    @Schema(description = "Delivery status", example = "IN_TRANSIT", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @Schema(description = "Delivery notes", example = "Leave at front door")
    private String notes;

    @Schema(description = "Estimated delivery date")
    private Instant estimatedDelivery;
}
