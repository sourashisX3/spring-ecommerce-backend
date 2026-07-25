package com.example.ecommerce_backend.modules.shipping.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class DeliveryRequest {

    @NotNull
    private Long shippingAddressId;

    @NotBlank
    private String carrierCode;

    private String trackingNumber;

    private Instant estimatedDelivery;
}
