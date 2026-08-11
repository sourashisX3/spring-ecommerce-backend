package com.example.ecommerce_backend.modules.wallet.mapper;

import com.example.ecommerce_backend.modules.wallet.dto.response.WalletResponse;
import com.example.ecommerce_backend.modules.wallet.entity.Wallet;

public class WalletMapper {

    private WalletMapper() {}

    public static WalletResponse toResponse(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .uuid(wallet.getUuid())
                .userId(wallet.getUser() != null ? wallet.getUser().getId() : null)
                .userName(wallet.getUser() != null
                        ? (wallet.getUser().getFirstName() + " " + wallet.getUser().getLastName()).trim()
                        : null)
                .userEmail(wallet.getUser() != null ? wallet.getUser().getEmail() : null)
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency() != null ? wallet.getCurrency().getCode() : null)
                .isActive(wallet.isActive())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .build();
    }
}
