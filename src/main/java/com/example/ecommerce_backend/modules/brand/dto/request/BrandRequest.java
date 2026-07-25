package com.example.ecommerce_backend.modules.brand.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BrandRequest {

    @NotBlank(message = "Brand name is required")
    private String name;

    private String description;
    private String logoUrl;
    private String website;
}
