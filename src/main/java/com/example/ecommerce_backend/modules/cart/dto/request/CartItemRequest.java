package com.example.ecommerce_backend.modules.cart.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Cart item request")
public class CartItemRequest {

    @Min(1)
    @Schema(description = "Quantity of the product", example = "1")
    private int quantity;
}
