package com.example.ecommerce_backend.modules.discount.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DiscountTypeRequest {
    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotBlank(message = "Computation type is required")
    private String computation;

    private String configSchema;
}
