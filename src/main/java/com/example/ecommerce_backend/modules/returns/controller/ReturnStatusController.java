package com.example.ecommerce_backend.modules.returns.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.StatusRequest;
import com.example.ecommerce_backend.modules.returns.service.ReturnStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/return-statuses")
@Tag(name = "Return Status", description = "Return status management APIs")
public class ReturnStatusController {

    @Autowired
    private ReturnStatusService returnStatusService;

    @PatchMapping("/{uuid}/status")
    @RequiresPermission("return:write")
    @Operation(summary = "Toggle return status", description = "Activates or deactivates a return status")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @PathVariable String uuid,
            @Valid @RequestBody StatusRequest request
    ) {
        boolean changed = returnStatusService.toggleStatus(uuid, request.isActive());
        String message = changed ? "Return status updated successfully" : "Return status is already " + (request.isActive() ? "active" : "inactive");
        return ApiResponse.success(null, message);
    }
}
