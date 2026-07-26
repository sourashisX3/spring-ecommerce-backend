package com.example.ecommerce_backend.modules.home.dto;

import com.example.ecommerce_backend.modules.brand.dto.response.BrandResponse;
import com.example.ecommerce_backend.modules.category.dto.response.CategoryResponse;
import com.example.ecommerce_backend.modules.product.dto.response.ProductResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Schema(description = "List of newly arrived products")
    private List<ProductResponse> newArrivals;
    @Schema(description = "List of featured products")
    private List<ProductResponse> featuredProducts;
}
