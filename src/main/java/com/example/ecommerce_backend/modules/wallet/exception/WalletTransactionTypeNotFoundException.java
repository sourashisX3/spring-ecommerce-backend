package com.example.ecommerce_backend.modules.wallet.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class WalletTransactionTypeNotFoundException extends BaseException {
    public WalletTransactionTypeNotFoundException(String code) {
        super("Wallet transaction type not found: " + code, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
