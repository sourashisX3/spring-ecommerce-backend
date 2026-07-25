package com.example.ecommerce_backend.modules.returns.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ReturnStatusNotFoundException extends BaseException {
    public ReturnStatusNotFoundException(String code) {
        super("Return status not found: " + code, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
