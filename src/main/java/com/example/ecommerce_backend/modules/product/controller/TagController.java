package com.example.ecommerce_backend.modules.product.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.product.dto.request.TagRequest;
import com.example.ecommerce_backend.modules.product.dto.response.TagResponse;
import com.example.ecommerce_backend.modules.product.service.TagService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping
    @RequiresPermission("tag:read")
    public ResponseEntity<ApiResponse<List<TagResponse>>> getAll() {
        List<TagResponse> tags = tagService.getAll();
        return ApiResponse.success(tags, "Tags retrieved successfully");
    }

    @PostMapping
    @RequiresPermission("tag:write")
    public ResponseEntity<ApiResponse<TagResponse>> create(@Valid @RequestBody TagRequest request) {
        TagResponse tag = tagService.create(request);
        return ApiResponse.created(tag, "Tag created successfully");
    }

    @DeleteMapping("/{slug}")
    @RequiresPermission("tag:write")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String slug) {
        tagService.delete(slug);
        return ApiResponse.success(null, "Tag deleted successfully");
    }
}
