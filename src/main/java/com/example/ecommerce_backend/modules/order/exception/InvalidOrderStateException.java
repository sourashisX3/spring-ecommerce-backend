package com.example.ecommerce_backend.modules.order.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidOrderStateException extends BaseException {

    public InvalidOrderStateException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
