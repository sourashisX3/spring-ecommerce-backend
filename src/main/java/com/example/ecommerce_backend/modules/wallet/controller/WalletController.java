package com.example.ecommerce_backend.modules.wallet.controller;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.Pagination;
import com.example.ecommerce_backend.core.dto.StatusRequest;
import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.wallet.dto.request.CreditRequest;
import com.example.ecommerce_backend.modules.wallet.dto.response.WalletResponse;
import com.example.ecommerce_backend.modules.wallet.dto.response.WalletTransactionResponse;
import com.example.ecommerce_backend.modules.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PatchMapping("/{uuid}/status")
    @RequiresPermission("wallet:write")
    @Operation(summary = "Toggle wallet status", description = "Activates or deactivates a wallet. When deactivated, debits are blocked.")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @PathVariable String uuid,
            @Valid @RequestBody StatusRequest request
    ) {
        boolean changed = walletService.toggleStatus(uuid, request.isActive());
        String message = changed ? "Wallet status updated successfully" : "Wallet is already " + (request.isActive() ? "active" : "inactive");
        return ApiResponse.success(null, message);
    }

    @GetMapping("/all")
    @RequiresPermission("wallet:read")
    @Operation(summary = "Get all wallets (admin)", description = "Retrieves all wallets with optional search by user name or email")
    public ResponseEntity<ApiResponse<List<WalletResponse>>> listAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String search
    ) {
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<WalletResponse> wallets = walletService.listAllWallets(search, pageable);
            return ApiResponse.paginated(wallets.getContent(), "Wallets retrieved successfully", Pagination.of(wallets));
        }
        List<WalletResponse> wallets = walletService.listAllWallets(search, Pageable.unpaged()).getContent();
        return ApiResponse.success(wallets, "Wallets retrieved successfully");
    }

    @GetMapping("/{uuid}/transactions")
    @RequiresPermission("wallet:read")
    @Operation(summary = "Get wallet transactions (admin)", description = "Retrieves transaction history for a wallet, with optional pagination")
    public ResponseEntity<ApiResponse<List<WalletTransactionResponse>>> getWalletTransactions(
            @PathVariable String uuid,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<WalletTransactionResponse> transactions = walletService.getWalletTransactionsByWalletUuid(uuid, pageable);
            return ApiResponse.paginated(transactions.getContent(), "Transactions retrieved successfully", Pagination.of(transactions));
        }
        List<WalletTransactionResponse> transactions = walletService.getWalletTransactionsByWalletUuid(uuid, Pageable.unpaged()).getContent();
        return ApiResponse.success(transactions, "Transactions retrieved successfully");
    }

    @PostMapping("/{uuid}/credit")
    @RequiresPermission("wallet:write")
    @Operation(summary = "Credit wallet (admin)", description = "Adds funds to a wallet (requires wallet:write permission)")
    public ResponseEntity<ApiResponse<WalletResponse>> credit(
            @PathVariable String uuid,
            @Valid @RequestBody CreditRequest request
    ) {
        WalletResponse wallet = walletService.creditWallet(uuid, request.getAmount(), request.getDescription());
        return ApiResponse.success(wallet, "Wallet credited successfully");
    }

    @PostMapping("/{uuid}/debit")
    @RequiresPermission("wallet:write")
    @Operation(summary = "Debit wallet (admin)", description = "Removes funds from a wallet (requires wallet:write permission)")
    public ResponseEntity<ApiResponse<WalletResponse>> debit(
            @PathVariable String uuid,
            @Valid @RequestBody CreditRequest request
    ) {
        WalletResponse wallet = walletService.debitWallet(uuid, request.getAmount(), request.getDescription());
        return ApiResponse.success(wallet, "Wallet debited successfully");
    }
}
