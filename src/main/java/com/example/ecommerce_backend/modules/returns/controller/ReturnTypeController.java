package com.example.ecommerce_backend.modules.returns.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.returns.dto.request.ReturnTypeRequest;
import com.example.ecommerce_backend.modules.returns.dto.response.ReturnTypeResponse;
import com.example.ecommerce_backend.modules.returns.service.ReturnTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/return-types")
public class ReturnTypeController {

    @Autowired
    private ReturnTypeService returnTypeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReturnTypeResponse>>> getAll() {
        List<ReturnTypeResponse> types = returnTypeService.getAll();
        return ApiResponse.success(types, "Return types retrieved successfully");
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<ReturnTypeResponse>> getByUuid(@PathVariable String uuid) {
        ReturnTypeResponse type = returnTypeService.getByUuid(uuid);
        return ApiResponse.success(type, "Return type retrieved successfully");
    }

    @PostMapping
    @RequiresPermission("return:write")
    public ResponseEntity<ApiResponse<ReturnTypeResponse>> create(@Valid @RequestBody ReturnTypeRequest request) {
        ReturnTypeResponse type = returnTypeService.create(request);
        return ApiResponse.created(type, "Return type created successfully");
    }

    @PutMapping("/{uuid}")
    @RequiresPermission("return:write")
    public ResponseEntity<ApiResponse<ReturnTypeResponse>> update(
            @PathVariable String uuid, @Valid @RequestBody ReturnTypeRequest request) {
        ReturnTypeResponse type = returnTypeService.update(uuid, request);
        return ApiResponse.success(type, "Return type updated successfully");
    }

    @DeleteMapping("/{uuid}")
    @RequiresPermission("return:write")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String uuid) {
        returnTypeService.delete(uuid);
        return ApiResponse.success(null, "Return type deleted successfully");
    }
}
