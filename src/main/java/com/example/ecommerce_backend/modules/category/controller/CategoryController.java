package com.example.ecommerce_backend.modules.category.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.category.dto.request.CategoryRequest;
import com.example.ecommerce_backend.modules.category.dto.response.CategoryResponse;
import com.example.ecommerce_backend.modules.category.service.CategoryService;
import com.example.ecommerce_backend.core.dto.StatusRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@Tag(name = "Category", description = "Category API")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get all categories", description = "Retrieves a list of all categories, optionally filtered by active status")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll(
            @RequestParam(required = false) Boolean active
    ) {
        List<CategoryResponse> categories = categoryService.getAll(active);
        return ApiResponse.success(categories, "Categories retrieved successfully");
    }

    @GetMapping("/tree")
    @Operation(summary = "Get category tree", description = "Retrieves the hierarchical category tree, optionally filtered by active status")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getTree(
            @RequestParam(required = false) Boolean active
    ) {
        List<CategoryResponse> tree = categoryService.getTree(active);
        return ApiResponse.success(tree, "Category tree retrieved successfully");
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get category by slug", description = "Retrieves a category by its unique slug")
    public ResponseEntity<ApiResponse<CategoryResponse>> getBySlug(@PathVariable String slug) {
        CategoryResponse category = categoryService.getBySlug(slug);
        return ApiResponse.success(category, "Category retrieved successfully");
    }

    @PostMapping
    @RequiresPermission("category:write")
    @Operation(summary = "Create a category", description = "Creates a new category with the provided details")
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse category = categoryService.create(request);
        return ApiResponse.created(category, "Category created successfully");
    }

    @PutMapping("/{uuid}")
    @RequiresPermission("category:write")
    @Operation(summary = "Update a category", description = "Updates an existing category identified by UUID with the provided details")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @PathVariable String uuid,
            @Valid @RequestBody CategoryRequest request
    ) {
        CategoryResponse category = categoryService.update(uuid, request);
        return ApiResponse.success(category, "Category updated successfully");
    }

    @PatchMapping("/{uuid}/status")
    @RequiresPermission("category:write")
    @Operation(summary = "Toggle category status", description = "Activates or deactivates a category identified by UUID")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @PathVariable String uuid,
            @Valid @RequestBody StatusRequest request
    ) {
        boolean changed = categoryService.toggleStatus(uuid, request.isActive());
        String message = changed ? "Category status updated successfully" : "Category is already " + (request.isActive() ? "active" : "inactive");
        return ApiResponse.success(null, message);
    }

    @DeleteMapping("/{uuid}")
    @RequiresPermission("category:write")
    @Operation(summary = "Delete a category", description = "Deletes a category identified by UUID")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String uuid) {
        categoryService.delete(uuid);
        return ApiResponse.success(null, "Category deleted successfully");
    }
}
