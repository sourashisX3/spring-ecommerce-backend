package com.example.ecommerce_backend.modules.product.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.product.dto.request.CategoryRequest;
import com.example.ecommerce_backend.modules.product.dto.request.StatusRequest;
import com.example.ecommerce_backend.modules.product.dto.response.CategoryResponse;
import com.example.ecommerce_backend.modules.product.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    @RequiresPermission("category:read")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll(
            @RequestParam(required = false) Boolean active
    ) {
        List<CategoryResponse> categories = categoryService.getAll(active);
        return ApiResponse.success(categories, "Categories retrieved successfully");
    }

    @GetMapping("/tree")
    @RequiresPermission("category:read")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getTree(
            @RequestParam(required = false) Boolean active
    ) {
        List<CategoryResponse> tree = categoryService.getTree(active);
        return ApiResponse.success(tree, "Category tree retrieved successfully");
    }

    @GetMapping("/{slug}")
    @RequiresPermission("category:read")
    public ResponseEntity<ApiResponse<CategoryResponse>> getBySlug(@PathVariable String slug) {
        CategoryResponse category = categoryService.getBySlug(slug);
        return ApiResponse.success(category, "Category retrieved successfully");
    }

    @PostMapping
    @RequiresPermission("category:write")
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse category = categoryService.create(request);
        return ApiResponse.created(category, "Category created successfully");
    }

    @PutMapping("/{slug}")
    @RequiresPermission("category:write")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @PathVariable String slug,
            @Valid @RequestBody CategoryRequest request
    ) {
        CategoryResponse category = categoryService.update(slug, request);
        return ApiResponse.success(category, "Category updated successfully");
    }

    @PatchMapping("/{slug}/status")
    @RequiresPermission("category:write")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @PathVariable String slug,
            @RequestBody StatusRequest request
    ) {
        boolean changed = categoryService.toggleStatus(slug, request.isActive());
        String message = changed ? "Category status updated successfully" : "Category is already " + (request.isActive() ? "active" : "inactive");
        return ApiResponse.success(null, message);
    }

    @DeleteMapping("/{slug}")
    @RequiresPermission("category:write")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String slug) {
        categoryService.delete(slug);
        return ApiResponse.success(null, "Category deleted successfully");
    }
}
