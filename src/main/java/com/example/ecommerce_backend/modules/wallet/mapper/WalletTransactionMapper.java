package com.example.ecommerce_backend.modules.wallet.mapper;

import com.example.ecommerce_backend.modules.wallet.dto.response.WalletTransactionResponse;
import com.example.ecommerce_backend.modules.wallet.entity.WalletTransaction;

public class WalletTransactionMapper {

    private WalletTransactionMapper() {}

    public static WalletTransactionResponse toResponse(WalletTransaction transaction) {
        return WalletTransactionResponse.builder()
                .id(transaction.getId())
                .uuid(transaction.getUuid())
                .type(transaction.getType() != null ? transaction.getType().getCode() : null)
                .amount(transaction.getAmount())
                .balanceBefore(transaction.getBalanceBefore())
                .balanceAfter(transaction.getBalanceAfter())
                .referenceType(transaction.getReferenceType())
                .referenceId(transaction.getReferenceId())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
