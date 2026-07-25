package com.example.ecommerce_backend.modules.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusHistoryResponse {

    private Long id;
    private String uuid;
    private OrderStatusResponse fromStatus;
    private OrderStatusResponse toStatus;
    private String changedBy;
    private String reason;
    private Instant createdAt;
}
