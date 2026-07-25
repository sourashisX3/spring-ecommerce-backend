package com.example.ecommerce_backend.modules.wallet.repository;

import com.example.ecommerce_backend.modules.wallet.entity.WalletTransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletTransactionTypeRepository extends JpaRepository<WalletTransactionType, Long> {

    Optional<WalletTransactionType> findByUuid(String uuid);

    Optional<WalletTransactionType> findByCode(String code);
}
