package com.example.ecommerce_backend.modules.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Order status request")
public class OrderStatusRequest {

    @NotBlank
    @Schema(description = "Status code", example = "PENDING", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @NotBlank
    @Schema(description = "Status name", example = "Pending", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Status description", example = "Order is pending")
    private String description;

    @Schema(description = "Sort order", example = "1")
    private int sortOrder;
}
