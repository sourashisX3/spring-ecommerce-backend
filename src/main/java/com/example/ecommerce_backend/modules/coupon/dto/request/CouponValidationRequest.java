package com.example.ecommerce_backend.modules.coupon.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CouponValidationRequest {

    @NotBlank(message = "Code is required")
    private String code;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Order subtotal is required")
    @Positive(message = "Order subtotal must be positive")
    private BigDecimal orderSubtotal;
}
