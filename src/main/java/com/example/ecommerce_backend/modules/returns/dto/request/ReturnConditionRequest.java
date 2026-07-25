package com.example.ecommerce_backend.modules.returns.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReturnConditionRequest {
    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String description;
}
