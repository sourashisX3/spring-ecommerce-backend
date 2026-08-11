package com.example.ecommerce_backend.modules.currency.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CurrencyDefaultException extends BaseException {
    public CurrencyDefaultException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
