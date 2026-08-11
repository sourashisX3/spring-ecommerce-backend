package com.example.ecommerce_backend.modules.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order response")
public class OrderResponse {

    @Schema(description = "Order ID", example = "1")
    private Long id;
    @Schema(description = "Order UUID", example = "order-uuid-123")
    private String uuid;
    @Schema(description = "Order number", example = "ORD-2026-001")
    private String orderNumber;
    @Schema(description = "Order status details")
    private OrderStatusResponse status;
    @Schema(description = "Customer summary")
    private OrderCustomerResponse customer;
    @Schema(description = "Subtotal amount", example = "199.99")
    private BigDecimal subtotal;
    @Schema(description = "Discount amount", example = "20.00")
    private BigDecimal discount;
    @Schema(description = "Shipping cost", example = "10.00")
    private BigDecimal shippingCost;
    @Schema(description = "Tax amount", example = "15.00")
    private BigDecimal tax;
    @Schema(description = "Total amount", example = "204.99")
    private BigDecimal total;
    @Schema(description = "Currency code", example = "USD")
    private String currency;
    @Schema(description = "Coupon code applied", example = "SUMMER20")
    private String couponCode;
    @Schema(description = "Order notes", example = "Leave at the door")
    private String notes;
    @Schema(description = "Order items")
    private List<OrderItemResponse> items;
    @Schema(description = "Order status history")
    private List<OrderStatusHistoryResponse> statusHistory;
    @Schema(description = "Creation timestamp")
    private Instant createdAt;
    @Schema(description = "Update timestamp")
    private Instant updatedAt;
    @Schema(description = "Cancellation timestamp")
    private Instant canceledAt;
}
