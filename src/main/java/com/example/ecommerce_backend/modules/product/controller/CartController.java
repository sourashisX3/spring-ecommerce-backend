package com.example.ecommerce_backend.modules.product.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.product.dto.request.CartItemRequest;
import com.example.ecommerce_backend.modules.product.dto.response.CartItemResponse;
import com.example.ecommerce_backend.modules.product.service.CartService;
import com.example.ecommerce_backend.modules.role_user.entity.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> getCart(
            @AuthenticationPrincipal User user
    ) {
        List<CartItemResponse> items = cartService.getCart(user);
        return ApiResponse.success(items, "Cart retrieved successfully");
    }

    @PostMapping("/{productUuid}")
    public ResponseEntity<ApiResponse<CartItemResponse>> addToCart(
            @PathVariable String productUuid,
            @Valid @RequestBody CartItemRequest request,
            @AuthenticationPrincipal User user
    ) {
        CartItemResponse item = cartService.addToCart(productUuid, request, user);
        return ApiResponse.created(item, "Added to cart successfully");
    }

    @PatchMapping("/{itemUuid}")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateQuantity(
            @PathVariable String itemUuid,
            @Valid @RequestBody CartItemRequest request,
            @AuthenticationPrincipal User user
    ) {
        CartItemResponse item = cartService.updateQuantity(itemUuid, request, user);
        return ApiResponse.success(item, "Quantity updated successfully");
    }

    @DeleteMapping("/{itemUuid}")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(
            @PathVariable String itemUuid,
            @AuthenticationPrincipal User user
    ) {
        cartService.removeFromCart(itemUuid, user);
        return ApiResponse.success(null, "Removed from cart successfully");
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @AuthenticationPrincipal User user
    ) {
        cartService.clearCart(user);
        return ApiResponse.success(null, "Cart cleared successfully");
    }
}