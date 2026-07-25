package com.example.ecommerce_backend.modules.returns.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.Pagination;
import com.example.ecommerce_backend.modules.returns.dto.request.ReturnRequestDto;
import com.example.ecommerce_backend.modules.returns.dto.request.UpdateReturnStatusRequest;
import com.example.ecommerce_backend.modules.returns.dto.response.ReturnResponse;
import com.example.ecommerce_backend.modules.returns.service.ReturnService;
import com.example.ecommerce_backend.modules.user.entity.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/returns")
public class ReturnController {

    @Autowired
    private ReturnService returnService;

    @GetMapping
    @RequiresPermission("return:read")
    public ResponseEntity<ApiResponse<List<ReturnResponse>>> getAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<ReturnResponse> returns = returnService.getAll(pageable);
            return ApiResponse.paginated(returns.getContent(), "Return requests retrieved successfully", Pagination.of(returns));
        }
        List<ReturnResponse> returns = returnService.getAll();
        return ApiResponse.success(returns, "Return requests retrieved successfully");
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<ReturnResponse>> getByUuid(@PathVariable String uuid) {
        ReturnResponse returnResponse = returnService.getByUuid(uuid);
        return ApiResponse.success(returnResponse, "Return request retrieved successfully");
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ReturnResponse>>> getMyReturns(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<ReturnResponse> returns = returnService.getByUserId(user.getId(), pageable);
            return ApiResponse.paginated(returns.getContent(), "My return requests retrieved successfully", Pagination.of(returns));
        }
        List<ReturnResponse> returns = returnService.getByUserId(user.getId());
        return ApiResponse.success(returns, "My return requests retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReturnResponse>> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ReturnRequestDto request) {
        ReturnResponse returnResponse = returnService.create(user, request);
        return ApiResponse.created(returnResponse, "Return request created successfully");
    }

    @PatchMapping("/{uuid}/status")
    @RequiresPermission("return:write")
    public ResponseEntity<ApiResponse<ReturnResponse>> updateStatus(
            @PathVariable String uuid,
            @Valid @RequestBody UpdateReturnStatusRequest request) {
        ReturnResponse returnResponse = returnService.updateStatus(uuid, request.getStatus(), request.getResolutionNotes());
        return ApiResponse.success(returnResponse, "Return request status updated successfully");
    }

    @DeleteMapping("/{uuid}")
    @RequiresPermission("return:write")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String uuid) {
        returnService.delete(uuid);
        return ApiResponse.success(null, "Return request deleted successfully");
    }
}
