package com.example.ecommerce_backend.modules.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private String imageUrl;
    private String parentSlug;
    private int sortOrder;
    private boolean isActive;
    private long productCount;
    private Instant createdAt;
    private Instant updatedAt;
    private List<CategoryResponse> children;
}
