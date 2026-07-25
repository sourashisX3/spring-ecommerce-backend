package com.example.ecommerce_backend.modules.shipping.mapper;

import com.example.ecommerce_backend.modules.shipping.dto.response.DeliveryResponse;
import com.example.ecommerce_backend.modules.shipping.dto.response.ShippingCarrierResponse;
import com.example.ecommerce_backend.modules.shipping.entity.Delivery;

public class DeliveryMapper {

    private DeliveryMapper() {
    }

    public static DeliveryResponse toResponse(Delivery delivery) {
        return DeliveryResponse.builder()
                .id(delivery.getId())
                .uuid(delivery.getUuid())
                .orderId(delivery.getOrderId())
                .shippingAddressId(delivery.getShippingAddress() != null
                        ? delivery.getShippingAddress().getId() : null)
                .carrier(delivery.getCarrier() != null
                        ? ShippingCarrierResponse.builder()
                                .id(delivery.getCarrier().getId())
                                .uuid(delivery.getCarrier().getUuid())
                                .code(delivery.getCarrier().getCode())
                                .name(delivery.getCarrier().getName())
                                .trackingUrlTemplate(delivery.getCarrier().getTrackingUrlTemplate())
                                .isActive(delivery.getCarrier().isActive())
                                .createdAt(delivery.getCarrier().getCreatedAt())
                                .updatedAt(delivery.getCarrier().getUpdatedAt())
                                .build()
                        : null)
                .trackingNumber(delivery.getTrackingNumber())
                .status(delivery.getStatus() != null ? delivery.getStatus().getCode() : null)
                .estimatedDelivery(delivery.getEstimatedDelivery())
                .shippedAt(delivery.getShippedAt())
                .deliveredAt(delivery.getDeliveredAt())
                .notes(delivery.getNotes())
                .createdAt(delivery.getCreatedAt())
                .updatedAt(delivery.getUpdatedAt())
                .build();
    }
}
