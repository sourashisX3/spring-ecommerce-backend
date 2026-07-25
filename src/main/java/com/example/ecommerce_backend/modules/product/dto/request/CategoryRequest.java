package com.example.ecommerce_backend.modules.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;
    private String imageUrl;
    private String parentSlug;
    private int sortOrder;
}
