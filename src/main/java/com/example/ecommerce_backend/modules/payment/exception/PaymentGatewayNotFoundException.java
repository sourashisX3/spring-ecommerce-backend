package com.example.ecommerce_backend.modules.payment.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class PaymentGatewayNotFoundException extends BaseException {
    public PaymentGatewayNotFoundException(String identifier) {
        super("Payment gateway not found: " + identifier, HttpStatus.NOT_FOUND);
    }
}
