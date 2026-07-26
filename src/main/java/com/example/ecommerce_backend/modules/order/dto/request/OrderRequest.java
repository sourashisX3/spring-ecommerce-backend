package com.example.ecommerce_backend.modules.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Order request")
public class OrderRequest {

    @NotNull
    @Schema(description = "Shipping address ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long shippingAddressId;

    @Schema(description = "Coupon code", example = "SUMMER20")
    private String couponCode;

    @Schema(description = "Order notes", example = "Leave at the door")
    private String notes;
}
