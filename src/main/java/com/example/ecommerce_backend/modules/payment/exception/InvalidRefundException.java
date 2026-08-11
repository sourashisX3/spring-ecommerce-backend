package com.example.ecommerce_backend.modules.payment.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidRefundException extends BaseException {
    public InvalidRefundException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
