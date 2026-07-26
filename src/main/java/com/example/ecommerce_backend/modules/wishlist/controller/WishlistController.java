package com.example.ecommerce_backend.modules.wishlist.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.wishlist.dto.response.WishlistItemResponse;
import com.example.ecommerce_backend.modules.wishlist.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlists")
@Tag(name = "Wishlist", description = "Wishlist management APIs")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping
    @Operation(summary = "Get wishlist", description = "Retrieves all items in the authenticated user's wishlist")
    public ResponseEntity<ApiResponse<List<WishlistItemResponse>>> getWishlist(
            @AuthenticationPrincipal User user
    ) {
        List<WishlistItemResponse> items = wishlistService.getWishlist(user);
        return ApiResponse.success(items, "Wishlist retrieved successfully");
    }

    @PostMapping("/{productUuid}")
    @Operation(summary = "Add to wishlist", description = "Adds a product to the authenticated user's wishlist")
    public ResponseEntity<ApiResponse<WishlistItemResponse>> addToWishlist(
            @PathVariable String productUuid,
            @AuthenticationPrincipal User user
    ) {
        WishlistItemResponse item = wishlistService.addToWishlist(productUuid, user);
        return ApiResponse.created(item, "Added to wishlist successfully");
    }

    @DeleteMapping("/{itemUuid}")
    @Operation(summary = "Remove from wishlist", description = "Removes an item from the authenticated user's wishlist")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(
            @PathVariable String itemUuid,
            @AuthenticationPrincipal User user
    ) {
        wishlistService.removeFromWishlist(itemUuid, user);
        return ApiResponse.success(null, "Removed from wishlist successfully");
    }
}
