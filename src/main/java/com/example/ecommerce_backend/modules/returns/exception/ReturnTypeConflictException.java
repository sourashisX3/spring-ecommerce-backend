package com.example.ecommerce_backend.modules.returns.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ReturnTypeConflictException extends BaseException {
    public ReturnTypeConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
