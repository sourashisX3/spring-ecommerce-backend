package com.example.ecommerce_backend.modules.coupon.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Request to validate and apply a coupon")
public class CouponValidationRequest {

    @NotBlank(message = "Code is required")
    @Schema(description = "Coupon code to validate", example = "SUMMER20", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @NotNull(message = "User ID is required")
    @Schema(description = "ID of the user applying the coupon", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @NotNull(message = "Order subtotal is required")
    @Positive(message = "Order subtotal must be positive")
    @Schema(description = "Order subtotal amount", example = "150.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal orderSubtotal;
}
