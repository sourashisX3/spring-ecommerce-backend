package com.example.ecommerce_backend.modules.product.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.product.dto.response.WishlistItemResponse;
import com.example.ecommerce_backend.modules.product.service.WishlistService;
import com.example.ecommerce_backend.modules.role_user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<WishlistItemResponse>>> getWishlist(
            @AuthenticationPrincipal User user
    ) {
        List<WishlistItemResponse> items = wishlistService.getWishlist(user);
        return ApiResponse.success(items, "Wishlist retrieved successfully");
    }

    @PostMapping("/{productUuid}")
    public ResponseEntity<ApiResponse<WishlistItemResponse>> addToWishlist(
            @PathVariable String productUuid,
            @AuthenticationPrincipal User user
    ) {
        WishlistItemResponse item = wishlistService.addToWishlist(productUuid, user);
        return ApiResponse.created(item, "Added to wishlist successfully");
    }

    @DeleteMapping("/{itemUuid}")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(
            @PathVariable String itemUuid,
            @AuthenticationPrincipal User user
    ) {
        wishlistService.removeFromWishlist(itemUuid, user);
        return ApiResponse.success(null, "Removed from wishlist successfully");
    }
}