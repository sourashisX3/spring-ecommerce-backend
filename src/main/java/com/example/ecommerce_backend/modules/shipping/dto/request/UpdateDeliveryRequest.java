package com.example.ecommerce_backend.modules.shipping.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;

@Data
public class UpdateDeliveryRequest {

    @NotBlank(message = "Carrier code is required")
    private String carrierCode;

    private String trackingNumber;

    @NotBlank(message = "Status is required")
    private String status;

    private String notes;

    private Instant estimatedDelivery;
}
