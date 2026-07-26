package com.example.ecommerce_backend.modules.shipping.dto.response;

import com.example.ecommerce_backend.modules.shipping.dto.response.ShippingCarrierResponse;
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
@Schema(description = "Delivery response")
public class DeliveryResponse {

    @Schema(description = "Delivery ID", example = "1")
    private Long id;
    @Schema(description = "Delivery UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String uuid;
    @Schema(description = "Order ID", example = "1")
    private Long orderId;
    @Schema(description = "Shipping address ID", example = "1")
    private Long shippingAddressId;
    @Schema(description = "Shipping carrier details")
    private ShippingCarrierResponse carrier;
    @Schema(description = "Tracking number", example = "1Z999AA10123456784")
    private String trackingNumber;
    @Schema(description = "Delivery status", example = "IN_TRANSIT")
    private String status;
    @Schema(description = "Estimated delivery date")
    private Instant estimatedDelivery;
    @Schema(description = "Shipped date")
    private Instant shippedAt;
    @Schema(description = "Delivered date")
    private Instant deliveredAt;
    @Schema(description = "Delivery notes", example = "Leave at front door")
    private String notes;
    @Schema(description = "Creation timestamp")
    private Instant createdAt;
    @Schema(description = "Last update timestamp")
    private Instant updatedAt;
}
