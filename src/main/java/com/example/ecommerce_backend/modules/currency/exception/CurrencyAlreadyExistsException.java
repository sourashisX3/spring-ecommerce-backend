package com.example.ecommerce_backend.modules.currency.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CurrencyAlreadyExistsException extends BaseException {
    public CurrencyAlreadyExistsException(String code) {
        super("Currency already exists: " + code, HttpStatus.CONFLICT);
    }
}
