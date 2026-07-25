package com.example.ecommerce_backend.modules.order.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {

    @NotNull
    private Long shippingAddressId;

    private String couponCode;

    private String notes;
}
