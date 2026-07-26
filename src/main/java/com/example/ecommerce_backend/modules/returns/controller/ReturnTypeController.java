package com.example.ecommerce_backend.modules.returns.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.StatusRequest;
import com.example.ecommerce_backend.modules.returns.dto.request.ReturnTypeRequest;
import com.example.ecommerce_backend.modules.returns.dto.response.ReturnTypeResponse;
import com.example.ecommerce_backend.modules.returns.service.ReturnTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/return-types")
@Tag(name = "Return Type", description = "Return Type API")
public class ReturnTypeController {

    @Autowired
    private ReturnTypeService returnTypeService;

    @GetMapping
    @Operation(summary = "Get all return types", description = "Retrieves all return types")
    public ResponseEntity<ApiResponse<List<ReturnTypeResponse>>> getAll() {
        List<ReturnTypeResponse> types = returnTypeService.getAll();
        return ApiResponse.success(types, "Return types retrieved successfully");
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get return type by UUID", description = "Retrieves a single return type by its UUID")
    public ResponseEntity<ApiResponse<ReturnTypeResponse>> getByUuid(@PathVariable String uuid) {
        ReturnTypeResponse type = returnTypeService.getByUuid(uuid);
        return ApiResponse.success(type, "Return type retrieved successfully");
    }

    @PostMapping
    @Operation(summary = "Create a return type", description = "Creates a new return type")
    @RequiresPermission("return:write")
    public ResponseEntity<ApiResponse<ReturnTypeResponse>> create(@Valid @RequestBody ReturnTypeRequest request) {
        ReturnTypeResponse type = returnTypeService.create(request);
        return ApiResponse.created(type, "Return type created successfully");
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update a return type", description = "Updates an existing return type by UUID")
    @RequiresPermission("return:write")
    public ResponseEntity<ApiResponse<ReturnTypeResponse>> update(
            @PathVariable String uuid, @Valid @RequestBody ReturnTypeRequest request) {
        ReturnTypeResponse type = returnTypeService.update(uuid, request);
        return ApiResponse.success(type, "Return type updated successfully");
    }

    @PatchMapping("/{uuid}/status")
    @Operation(summary = "Toggle return type status", description = "Activates or deactivates a return type")
    @RequiresPermission("return:write")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @PathVariable String uuid,
            @Valid @RequestBody StatusRequest request
    ) {
        boolean changed = returnTypeService.toggleStatus(uuid, request.isActive());
        String message = changed ? "Return type status updated successfully" : "Return type is already " + (request.isActive() ? "active" : "inactive");
        return ApiResponse.success(null, message);
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete a return type", description = "Deletes a return type by UUID")
    @RequiresPermission("return:write")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String uuid) {
        returnTypeService.delete(uuid);
        return ApiResponse.success(null, "Return type deleted successfully");
    }
}
