package com.example.ecommerce_backend.modules.home.dto;

import com.example.ecommerce_backend.modules.brand.dto.response.BrandResponse;
import com.example.ecommerce_backend.modules.category.dto.response.CategoryResponse;
import com.example.ecommerce_backend.modules.product.dto.response.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HomeResponse {
    private List<CategoryResponse> categories;
    private List<BrandResponse> brands;
    private List<ProductResponse> newArrivals;
    private List<ProductResponse> featuredProducts;
}
