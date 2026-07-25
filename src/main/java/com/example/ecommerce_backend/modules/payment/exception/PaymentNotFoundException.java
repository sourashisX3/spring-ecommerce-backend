package com.example.ecommerce_backend.modules.payment.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class PaymentNotFoundException extends BaseException {
    public PaymentNotFoundException(String identifier) {
        super("Payment not found: " + identifier, HttpStatus.NOT_FOUND);
    }
}
