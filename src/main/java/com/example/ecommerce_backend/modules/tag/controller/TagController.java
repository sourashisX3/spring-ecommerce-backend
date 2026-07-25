package com.example.ecommerce_backend.modules.tag.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.StatusRequest;
import com.example.ecommerce_backend.modules.tag.dto.request.TagRequest;
import com.example.ecommerce_backend.modules.tag.dto.response.TagResponse;
import com.example.ecommerce_backend.modules.tag.service.TagService;
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
    public ResponseEntity<ApiResponse<List<TagResponse>>> getAll(
            @RequestParam(required = false) Boolean active
    ) {
        List<TagResponse> tags = tagService.getAll(active);
        return ApiResponse.success(tags, "Tags retrieved successfully");
    }

    @PostMapping
    @RequiresPermission("tag:write")
    public ResponseEntity<ApiResponse<TagResponse>> create(@Valid @RequestBody TagRequest request) {
        TagResponse tag = tagService.create(request);
        return ApiResponse.created(tag, "Tag created successfully");
    }

    @PutMapping("/{uuid}")
    @RequiresPermission("tag:write")
    public ResponseEntity<ApiResponse<TagResponse>> update(
            @PathVariable String uuid,
            @Valid @RequestBody TagRequest request
    ) {
        TagResponse tag = tagService.update(uuid, request);
        return ApiResponse.success(tag, "Tag updated successfully");
    }

    @PatchMapping("/{uuid}/status")
    @RequiresPermission("tag:write")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @PathVariable String uuid,
            @Valid @RequestBody StatusRequest request
    ) {
        boolean changed = tagService.toggleStatus(uuid, request.isActive());
        String message = changed ? "Tag status updated successfully" : "Tag is already " + (request.isActive() ? "active" : "inactive");
        return ApiResponse.success(null, message);
    }

    @DeleteMapping("/{uuid}")
    @RequiresPermission("tag:write")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String uuid) {
        tagService.delete(uuid);
        return ApiResponse.success(null, "Tag deleted successfully");
    }
}
