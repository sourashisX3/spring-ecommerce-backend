package com.example.ecommerce_backend.modules.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Update order status request")
public class UpdateOrderStatusRequest {

    @NotBlank
    @Schema(description = "New status code", example = "SHIPPED", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @Schema(description = "Reason for status change", example = "Item is out of stock")
    private String reason;
}
