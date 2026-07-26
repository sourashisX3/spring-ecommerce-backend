package com.example.ecommerce_backend.modules.returns.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ReturnConditionNotFoundException extends BaseException {
    public ReturnConditionNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
