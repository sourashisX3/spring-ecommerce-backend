package com.example.ecommerce_backend.modules.currency.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CurrencyNotFoundException extends BaseException {
    public CurrencyNotFoundException(String code) {
        super("Currency not found: " + code, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
