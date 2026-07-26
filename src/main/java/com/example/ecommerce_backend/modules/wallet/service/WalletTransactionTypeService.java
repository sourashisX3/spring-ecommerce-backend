package com.example.ecommerce_backend.modules.wallet.service;

import com.example.ecommerce_backend.modules.wallet.entity.WalletTransactionType;
import com.example.ecommerce_backend.modules.wallet.exception.WalletTransactionTypeNotFoundException;
import com.example.ecommerce_backend.modules.wallet.repository.WalletTransactionTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletTransactionTypeService {

    @Autowired
    private WalletTransactionTypeRepository walletTransactionTypeRepository;

    @Transactional
    public boolean toggleStatus(String uuid, boolean isActive) {
        WalletTransactionType type = walletTransactionTypeRepository.findByUuid(uuid)
                .orElseThrow(() -> new WalletTransactionTypeNotFoundException("Wallet transaction type not found: " + uuid));
        if (type.isActive() == isActive) {
            return false;
        }
        type.setActive(isActive);
        walletTransactionTypeRepository.save(type);
        return true;
    }
}
