package com.example.ecommerce_backend.modules.tag.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TagRequest {

    @NotBlank(message = "Tag name is required")
    private String name;
}
