package com.example.ecommerce_backend.modules.order.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InsufficientStockException extends BaseException {

    public InsufficientStockException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
