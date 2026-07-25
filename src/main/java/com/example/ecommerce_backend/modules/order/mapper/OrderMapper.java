package com.example.ecommerce_backend.modules.order.mapper;

import com.example.ecommerce_backend.modules.order.dto.response.OrderItemResponse;
import com.example.ecommerce_backend.modules.order.dto.response.OrderResponse;
import com.example.ecommerce_backend.modules.order.dto.response.OrderStatusHistoryResponse;
import com.example.ecommerce_backend.modules.order.dto.response.OrderStatusResponse;
import com.example.ecommerce_backend.modules.order.entity.Order;
import com.example.ecommerce_backend.modules.order.entity.OrderItem;
import com.example.ecommerce_backend.modules.order.entity.OrderStatus;
import com.example.ecommerce_backend.modules.order.entity.OrderStatusHistory;

import java.util.stream.Collectors;

public class OrderMapper {

    private OrderMapper() {
    }

    public static OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .uuid(order.getUuid())
                .orderNumber(order.getOrderNumber())
                .status(toStatusResponse(order.getStatus()))
                .subtotal(order.getSubtotal())
                .discount(order.getDiscount())
                .shippingCost(order.getShippingCost())
                .tax(order.getTax())
                .total(order.getTotal())
                .currency(order.getCurrency() != null ? order.getCurrency().getCode() : null)
                .couponCode(order.getCouponCode())
                .notes(order.getNotes())
                .items(order.getItems().stream()
                        .map(OrderMapper::toOrderItemResponse)
                        .collect(Collectors.toList()))
                .statusHistory(order.getStatusHistory().stream()
                        .map(OrderMapper::toStatusHistoryResponse)
                        .collect(Collectors.toList()))
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .canceledAt(order.getCanceledAt())
                .build();
    }

    public static OrderItemResponse toOrderItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .uuid(item.getUuid())
                .productId(item.getProductId())
                .variantId(item.getVariantId())
                .productName(item.getProductName())
                .variantName(item.getVariantName())
                .sku(item.getSku())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }

    public static OrderStatusHistoryResponse toStatusHistoryResponse(OrderStatusHistory history) {
        return OrderStatusHistoryResponse.builder()
                .id(history.getId())
                .uuid(history.getUuid())
                .fromStatus(toStatusResponse(history.getFromStatus()))
                .toStatus(toStatusResponse(history.getToStatus()))
                .changedBy(history.getChangedBy())
                .reason(history.getReason())
                .createdAt(history.getCreatedAt())
                .build();
    }

    private static OrderStatusResponse toStatusResponse(OrderStatus s) {
        if (s == null) return null;
        return OrderStatusResponse.builder()
                .id(s.getId())
                .uuid(s.getUuid())
                .code(s.getCode())
                .name(s.getName())
                .description(s.getDescription())
                .sortOrder(s.getSortOrder())
                .isActive(s.isActive())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}
