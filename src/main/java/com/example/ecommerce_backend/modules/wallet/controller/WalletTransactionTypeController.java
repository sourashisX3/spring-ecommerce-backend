package com.example.ecommerce_backend.modules.wallet.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.StatusRequest;
import com.example.ecommerce_backend.modules.wallet.service.WalletTransactionTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet-transaction-types")
@Tag(name = "Wallet Transaction Type", description = "Wallet transaction type management APIs")
public class WalletTransactionTypeController {

    @Autowired
    private WalletTransactionTypeService walletTransactionTypeService;

    @PatchMapping("/{uuid}/status")
    @RequiresPermission("wallet:write")
    @Operation(summary = "Toggle wallet transaction type status", description = "Activates or deactivates a wallet transaction type")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @PathVariable String uuid,
            @Valid @RequestBody StatusRequest request
    ) {
        boolean changed = walletTransactionTypeService.toggleStatus(uuid, request.isActive());
        String message = changed ? "Wallet transaction type status updated successfully" : "Wallet transaction type is already " + (request.isActive() ? "active" : "inactive");
        return ApiResponse.success(null, message);
    }
}
