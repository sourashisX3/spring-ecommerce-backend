package com.example.ecommerce_backend.modules.returns.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ReturnStatusNotActiveException extends BaseException {
    public ReturnStatusNotActiveException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
