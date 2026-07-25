package com.example.ecommerce_backend.modules.returns.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.modules.returns.dto.request.ReturnConditionRequest;
import com.example.ecommerce_backend.modules.returns.dto.response.ReturnConditionResponse;
import com.example.ecommerce_backend.modules.returns.service.ReturnConditionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/return-conditions")
public class ReturnConditionController {

    @Autowired
    private ReturnConditionService returnConditionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReturnConditionResponse>>> getAll() {
        List<ReturnConditionResponse> conditions = returnConditionService.getAll();
        return ApiResponse.success(conditions, "Return conditions retrieved successfully");
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<ReturnConditionResponse>> getByUuid(@PathVariable String uuid) {
        ReturnConditionResponse condition = returnConditionService.getByUuid(uuid);
        return ApiResponse.success(condition, "Return condition retrieved successfully");
    }

    @PostMapping
    @RequiresPermission("return:write")
    public ResponseEntity<ApiResponse<ReturnConditionResponse>> create(@Valid @RequestBody ReturnConditionRequest request) {
        ReturnConditionResponse condition = returnConditionService.create(request);
        return ApiResponse.created(condition, "Return condition created successfully");
    }

    @PutMapping("/{uuid}")
    @RequiresPermission("return:write")
    public ResponseEntity<ApiResponse<ReturnConditionResponse>> update(
            @PathVariable String uuid, @Valid @RequestBody ReturnConditionRequest request) {
        ReturnConditionResponse condition = returnConditionService.update(uuid, request);
        return ApiResponse.success(condition, "Return condition updated successfully");
    }

    @DeleteMapping("/{uuid}")
    @RequiresPermission("return:write")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String uuid) {
        returnConditionService.delete(uuid);
        return ApiResponse.success(null, "Return condition deleted successfully");
    }
}
