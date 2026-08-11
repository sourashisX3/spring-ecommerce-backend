package com.example.ecommerce_backend.modules.tag.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.Pagination;
import com.example.ecommerce_backend.core.dto.StatusRequest;
import com.example.ecommerce_backend.modules.tag.dto.request.TagRequest;
import com.example.ecommerce_backend.modules.tag.dto.response.TagResponse;
import com.example.ecommerce_backend.modules.tag.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
@Tag(name = "Tags", description = "Tag management APIs")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping
    @Operation(summary = "Get all tags", description = "Retrieves tags with optional search, active filter, sorting and pagination")
    public ResponseEntity<ApiResponse<List<TagResponse>>> getAll(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        if (sortBy != null) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortDir)
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = Sort.by(direction, sortBy);
        }

        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<TagResponse> tags = tagService.getAll(search, active, pageable);
            return ApiResponse.paginated(
                    tags.getContent(),
                    "Tags retrieved successfully",
                    Pagination.of(tags)
            );
        }
        List<TagResponse> tags = tagService.getAll(active);
        return ApiResponse.success(tags, "Tags retrieved successfully");
    }

    @PostMapping
    @RequiresPermission("tag:write")
    @Operation(summary = "Create tag", description = "Creates a new tag")
    public ResponseEntity<ApiResponse<TagResponse>> create(@Valid @RequestBody TagRequest request) {
        TagResponse tag = tagService.create(request);
        return ApiResponse.created(tag, "Tag created successfully");
    }

    @PutMapping("/{uuid}")
    @RequiresPermission("tag:write")
    @Operation(summary = "Update tag", description = "Updates an existing tag")
    public ResponseEntity<ApiResponse<TagResponse>> update(
            @PathVariable String uuid,
            @Valid @RequestBody TagRequest request
    ) {
        TagResponse tag = tagService.update(uuid, request);
        return ApiResponse.success(tag, "Tag updated successfully");
    }

    @PatchMapping("/{uuid}/status")
    @RequiresPermission("tag:write")
    @Operation(summary = "Toggle tag status", description = "Toggles the active status of a tag")
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
    @Operation(summary = "Delete tag", description = "Deletes a tag")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String uuid) {
        tagService.delete(uuid);
        return ApiResponse.success(null, "Tag deleted successfully");
    }
}
