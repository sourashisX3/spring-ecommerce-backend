package com.example.ecommerce_backend.modules.wallet.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class WalletNotFoundException extends BaseException {

    public WalletNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
