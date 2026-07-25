package com.example.ecommerce_backend.modules.cart.dto.response;

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
public class CartItemResponse {

    private String uuid;
    private String productUuid;
    private String productName;
    private String productSlug;
    private String productImage;
    private BigDecimal productPrice;
    private int quantity;
    private Instant createdAt;
    private Instant updatedAt;
}
