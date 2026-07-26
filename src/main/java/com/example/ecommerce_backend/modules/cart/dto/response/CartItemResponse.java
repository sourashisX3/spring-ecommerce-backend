package com.example.ecommerce_backend.modules.cart.dto.response;

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
@Schema(description = "Cart item response")
public class CartItemResponse {

    @Schema(description = "Cart item UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String uuid;

    @Schema(description = "Product UUID", example = "p1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String productUuid;

    @Schema(description = "Product name", example = "Nike Air Max")
    private String productName;

    @Schema(description = "Product slug", example = "nike-air-max")
    private String productSlug;

    @Schema(description = "Product image URL", example = "https://example.com/image.png")
    private String productImage;

    @Schema(description = "Product price", example = "99.99")
    private BigDecimal productPrice;

    @Schema(description = "Quantity", example = "2")
    private int quantity;

    @Schema(description = "Creation timestamp")
    private Instant createdAt;

    @Schema(description = "Last update timestamp")
    private Instant updatedAt;
}
