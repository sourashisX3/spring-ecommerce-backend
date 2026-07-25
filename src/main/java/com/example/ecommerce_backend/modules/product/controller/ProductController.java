package com.example.ecommerce_backend.modules.product.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.Pagination;
import com.example.ecommerce_backend.modules.product.dto.request.ProductRequest;
import com.example.ecommerce_backend.modules.product.dto.request.StatusRequest;
import com.example.ecommerce_backend.modules.product.dto.response.ProductResponse;
import com.example.ecommerce_backend.modules.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts(
            @RequestParam(required = false) String categorySlug,
            @RequestParam(required = false) String brandSlug,
            @RequestParam(required = false) String tagSlug,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean isFeatured,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String attrColor,
            @RequestParam(required = false) String attrSize,
            @RequestParam(required = false) String attrMaterial
    ) {
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        if (sortBy != null) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortDir)
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = Sort.by(direction, sortBy);
        }
        Pageable pageable = PageRequest.of(page, size, sort);

        Map<String, String> attributeFilters = new HashMap<>();
        if (attrColor != null) attributeFilters.put("color", attrColor);
        if (attrSize != null) attributeFilters.put("size", attrSize);
        if (attrMaterial != null) attributeFilters.put("material", attrMaterial);

        Page<ProductResponse> products = productService.getAllProducts(
                categorySlug, brandSlug, tagSlug, search,
                minPrice, maxPrice, isFeatured, active,
                attributeFilters.isEmpty() ? null : attributeFilters,
                pageable
        );

        return ApiResponse.paginated(
                products.getContent(),
                "Products retrieved successfully",
                Pagination.of(products)
        );
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<ProductResponse>> getByUuid(
            @PathVariable String uuid,
            @RequestParam(required = false) String attrColor,
            @RequestParam(required = false) String attrSize,
            @RequestParam(required = false) String attrMaterial
    ) {
        Map<String, String> attributeFilters = new HashMap<>();
        if (attrColor != null) attributeFilters.put("color", attrColor);
        if (attrSize != null) attributeFilters.put("size", attrSize);
        if (attrMaterial != null) attributeFilters.put("material", attrMaterial);

        ProductResponse product = productService.getByUuid(
                uuid,
                attributeFilters.isEmpty() ? null : attributeFilters
        );
        return ApiResponse.success(product, "Product retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(@RequestBody ProductRequest request) {
        ProductResponse product = productService.create(request);
        return ApiResponse.created(product, "Product created successfully");
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable String uuid,
            @RequestBody ProductRequest request
    ) {
        ProductResponse product = productService.update(uuid, request);
        return ApiResponse.success(product, "Product updated successfully");
    }

    @PatchMapping("/{uuid}/status")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @PathVariable String uuid,
            @RequestBody StatusRequest request
    ) {
        boolean changed = productService.toggleStatus(uuid, request.isActive());
        String message = changed ? "Product status updated successfully" : "Product is already " + (request.isActive() ? "active" : "inactive");
        return ApiResponse.success(null, message);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String uuid) {
        productService.delete(uuid);
        return ApiResponse.success(null, "Product deleted successfully");
    }

    @GetMapping("/{uuid}/similar")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getSimilarProducts(
            @PathVariable String uuid,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<ProductResponse> products = productService.getSimilarProducts(uuid, limit);
        return ApiResponse.success(products, "Similar products retrieved successfully");
    }
}
