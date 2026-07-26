package com.example.ecommerce_backend.modules.cart.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ProductNotActiveException extends BaseException {
    public ProductNotActiveException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
