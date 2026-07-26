package com.example.ecommerce_backend.modules.returns.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ReturnTypeNotFoundException extends BaseException {
    public ReturnTypeNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
