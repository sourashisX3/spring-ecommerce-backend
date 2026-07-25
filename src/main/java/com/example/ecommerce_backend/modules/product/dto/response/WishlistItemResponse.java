package com.example.ecommerce_backend.modules.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemResponse {

    private String uuid;
    private String productUuid;
    private String productName;
    private String productSlug;
    private String productImage;
    private Instant createdAt;
}