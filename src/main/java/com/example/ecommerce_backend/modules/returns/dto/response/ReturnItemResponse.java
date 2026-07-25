package com.example.ecommerce_backend.modules.returns.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnItemResponse {
    private String uuid;
    private Long orderItemId;
    private Long productId;
    private String productName;
    private String sku;
    private int quantity;
    private BigDecimal unitPrice;
    private ReturnConditionResponse condition;
    private String conditionNote;
    private Instant createdAt;
}
