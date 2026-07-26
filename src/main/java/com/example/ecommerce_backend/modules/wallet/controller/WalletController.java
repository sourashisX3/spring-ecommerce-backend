package com.example.ecommerce_backend.modules.wallet.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.Pagination;
import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.wallet.dto.response.WalletResponse;
import com.example.ecommerce_backend.modules.wallet.dto.response.WalletTransactionResponse;
import com.example.ecommerce_backend.modules.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wallet")
@Tag(name = "Wallet", description = "Wallet management APIs")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @GetMapping
    @Operation(summary = "Get wallet", description = "Retrieves the wallet of the authenticated user")
    public ResponseEntity<ApiResponse<WalletResponse>> getWallet(
            @AuthenticationPrincipal User user
    ) {
        WalletResponse wallet = walletService.getWallet(user.getId());
        return ApiResponse.success(wallet, "Wallet retrieved successfully");
    }

    @GetMapping("/transactions")
    @Operation(summary = "Get wallet transactions", description = "Retrieves transaction history for the authenticated user's wallet, with optional pagination")
    public ResponseEntity<ApiResponse<List<WalletTransactionResponse>>> getTransactions(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<WalletTransactionResponse> transactions = walletService.getTransactions(user.getId(), pageable);
            return ApiResponse.paginated(transactions.getContent(), "Transactions retrieved successfully", Pagination.of(transactions));
        }
        List<WalletTransactionResponse> transactions = walletService.getTransactions(user.getId());
        return ApiResponse.success(transactions, "Transactions retrieved successfully");
    }
}
