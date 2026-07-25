package com.example.ecommerce_backend.modules.shipping.dto.response;

import com.example.ecommerce_backend.modules.shipping.dto.response.ShippingCarrierResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryResponse {

    private Long id;
    private String uuid;
    private Long orderId;
    private Long shippingAddressId;
    private ShippingCarrierResponse carrier;
    private String trackingNumber;
    private String status;
    private Instant estimatedDelivery;
    private Instant shippedAt;
    private Instant deliveredAt;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;
}
