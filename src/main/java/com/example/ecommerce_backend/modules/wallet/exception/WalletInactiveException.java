package com.example.ecommerce_backend.modules.wallet.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class WalletInactiveException extends BaseException {

    public WalletInactiveException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
