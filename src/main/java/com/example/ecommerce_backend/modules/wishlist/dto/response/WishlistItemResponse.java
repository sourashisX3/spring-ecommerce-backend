package com.example.ecommerce_backend.modules.wishlist.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Wishlist item response")
public class WishlistItemResponse {

    @Schema(description = "Wishlist item UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String uuid;
    @Schema(description = "Product UUID", example = "550e8400-e29b-41d4-a716-446655440001")
    private String productUuid;
    @Schema(description = "Product name", example = "Wireless Headphones")
    private String productName;
    @Schema(description = "Product slug", example = "wireless-headphones")
    private String productSlug;
    @Schema(description = "Product image URL", example = "https://example.com/image.jpg")
    private String productImage;
    @Schema(description = "Creation timestamp")
    private Instant createdAt;
}
