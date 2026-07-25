package com.example.ecommerce_backend.modules.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderStatusRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String description;

    private int sortOrder;
}
