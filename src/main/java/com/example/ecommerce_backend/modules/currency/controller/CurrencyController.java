package com.example.ecommerce_backend.modules.currency.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.StatusRequest;
import com.example.ecommerce_backend.modules.currency.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/currencies")
@Tag(name = "Currency", description = "Currency management APIs")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    @PatchMapping("/{uuid}/status")
    @RequiresPermission("currency:write")
    @Operation(summary = "Toggle currency status", description = "Activates or deactivates a currency")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @PathVariable String uuid,
            @Valid @RequestBody StatusRequest request
    ) {
        boolean changed = currencyService.toggleStatus(uuid, request.isActive());
        String message = changed ? "Currency status updated successfully" : "Currency is already " + (request.isActive() ? "active" : "inactive");
        return ApiResponse.success(null, message);
    }
}
