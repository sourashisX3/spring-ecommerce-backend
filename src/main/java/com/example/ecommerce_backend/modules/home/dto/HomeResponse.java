package com.example.ecommerce_backend.modules.home.dto;

import com.example.ecommerce_backend.modules.banner.dto.response.BannerResponse;
import com.example.ecommerce_backend.modules.brand.dto.response.BrandResponse;
import com.example.ecommerce_backend.modules.category.dto.response.CategoryResponse;
import com.example.ecommerce_backend.modules.offer.dto.response.OfferResponse;
import com.example.ecommerce_backend.modules.product.dto.response.ProductResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response containing home page data")
public class HomeResponse {
    @Schema(description = "List of product categories")
    private List<CategoryResponse> categories;
    @Schema(description = "List of brands")
    private List<BrandResponse> brands;
    @Schema(description = "List of active banners")
    private List<BannerResponse> banners;
    @Schema(description = "List of eligible offers")
    private List<OfferResponse> offers;
    @Schema(description = "List of newly arrived products")
    private List<ProductResponse> newArrivals;
    @Schema(description = "List of featured products")
    private List<ProductResponse> featuredProducts;
    @Schema(description = "List of best selling products")
    private List<ProductResponse> bestSellers;
    @Schema(description = "List of trending products")
    private List<ProductResponse> trending;
    @Schema(description = "List of discounted products")
    private List<ProductResponse> deals;
    @Schema(description = "Authenticated user wallet balance")
    private BigDecimal walletBalance;
    @Schema(description = "Authenticated user cart item count")
    private long cartCount;
    @Schema(description = "Authenticated user wishlist item count")
    private long wishlistCount;
    @Schema(description = "Authenticated user unread notification count")
    private long unreadNotificationCount;
}
