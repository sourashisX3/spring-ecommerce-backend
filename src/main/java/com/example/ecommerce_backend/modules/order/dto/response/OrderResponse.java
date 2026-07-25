package com.example.ecommerce_backend.modules.order.dto.response;

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
public class OrderResponse {

    private Long id;
    private String uuid;
    private String orderNumber;
    private OrderStatusResponse status;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal shippingCost;
    private BigDecimal tax;
    private BigDecimal total;
    private String currency;
    private String couponCode;
    private String notes;
    private List<OrderItemResponse> items;
    private List<OrderStatusHistoryResponse> statusHistory;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant canceledAt;
}
