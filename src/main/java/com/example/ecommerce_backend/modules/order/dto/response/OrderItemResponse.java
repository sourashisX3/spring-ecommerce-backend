package com.example.ecommerce_backend.modules.order.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order item response")
public class OrderItemResponse {

    @Schema(description = "Item ID", example = "1")
    private Long id;
    @Schema(description = "Item UUID", example = "item-uuid-123")
    private String uuid;
    @Schema(description = "Product ID", example = "1")
    private Long productId;
    @Schema(description = "Variant ID", example = "1")
    private Long variantId;
    @Schema(description = "Product name", example = "Wireless Mouse")
    private String productName;
    @Schema(description = "Variant name", example = "Black")
    private String variantName;
    @Schema(description = "SKU", example = "WM-BLK-001")
    private String sku;
    @Schema(description = "Quantity", example = "2")
    private int quantity;
    @Schema(description = "Unit price", example = "29.99")
    private BigDecimal unitPrice;
    @Schema(description = "Total price", example = "59.98")
    private BigDecimal totalPrice;
}
