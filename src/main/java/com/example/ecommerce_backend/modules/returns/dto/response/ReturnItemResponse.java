package com.example.ecommerce_backend.modules.returns.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object for a return item")
public class ReturnItemResponse {
    @Schema(description = "Return item UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String uuid;
    @Schema(description = "Order item ID", example = "1")
    private Long orderItemId;
    @Schema(description = "Product ID", example = "1")
    private Long productId;
    @Schema(description = "Product name", example = "Wireless Headphones")
    private String productName;
    @Schema(description = "Product SKU", example = "WH-1000XM5")
    private String sku;
    @Schema(description = "Quantity returned", example = "1")
    private int quantity;
    @Schema(description = "Unit price at time of purchase", example = "99.99")
    private BigDecimal unitPrice;
    @Schema(description = "Return condition")
    private ReturnConditionResponse condition;
    @Schema(description = "Note about the item condition", example = "Item arrived damaged")
    private String conditionNote;
    @Schema(description = "Creation timestamp")
    private Instant createdAt;
}
