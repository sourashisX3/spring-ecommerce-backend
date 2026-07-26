package com.example.ecommerce_backend.modules.cart.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.cart.dto.request.CartItemRequest;
import com.example.ecommerce_backend.modules.cart.dto.response.CartItemResponse;
import com.example.ecommerce_backend.modules.cart.service.CartService;
import com.example.ecommerce_backend.modules.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts")
@Tag(name = "Cart", description = "Cart API")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    @Operation(summary = "Get user cart", description = "Retrieves all cart items for the authenticated user")
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> getCart(
            @AuthenticationPrincipal User user
    ) {
        List<CartItemResponse> items = cartService.getCart(user);
        return ApiResponse.success(items, "Cart retrieved successfully");
    }

    @PostMapping("/{productUuid}")
    @Operation(summary = "Add item to cart", description = "Adds a product to the authenticated user's cart")
    public ResponseEntity<ApiResponse<CartItemResponse>> addToCart(
            @PathVariable String productUuid,
            @Valid @RequestBody CartItemRequest request,
            @AuthenticationPrincipal User user
    ) {
        CartItemResponse item = cartService.addToCart(productUuid, request, user);
        return ApiResponse.created(item, "Added to cart successfully");
    }

    @PatchMapping("/{itemUuid}")
    @Operation(summary = "Update cart item quantity", description = "Updates the quantity of a specific cart item for the authenticated user")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateQuantity(
            @PathVariable String itemUuid,
            @Valid @RequestBody CartItemRequest request,
            @AuthenticationPrincipal User user
    ) {
        CartItemResponse item = cartService.updateQuantity(itemUuid, request, user);
        return ApiResponse.success(item, "Quantity updated successfully");
    }

    @DeleteMapping("/{itemUuid}")
    @Operation(summary = "Remove item from cart", description = "Removes a specific item from the authenticated user's cart")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(
            @PathVariable String itemUuid,
            @AuthenticationPrincipal User user
    ) {
        cartService.removeFromCart(itemUuid, user);
        return ApiResponse.success(null, "Removed from cart successfully");
    }

    @DeleteMapping
    @Operation(summary = "Clear cart", description = "Removes all items from the authenticated user's cart")
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @AuthenticationPrincipal User user
    ) {
        cartService.clearCart(user);
        return ApiResponse.success(null, "Cart cleared successfully");
    }
}
