package com.example.ecommerce_backend.modules.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariantResponse {

    private Long id;
    private String sku;
    private String name;
    private BigDecimal price;
    private int stock;
    private Map<String, String> attributes;
    private boolean isActive;
    private boolean isDefault;
    private boolean selected;
    private int sortOrder;
    private Instant createdAt;
    private Instant updatedAt;
}
