package com.example.ecommerce_backend.modules.returns.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ReturnNotFoundException extends BaseException {
    public ReturnNotFoundException(String uuid) {
        super("Return request not found: " + uuid, HttpStatus.NOT_FOUND);
    }
}
