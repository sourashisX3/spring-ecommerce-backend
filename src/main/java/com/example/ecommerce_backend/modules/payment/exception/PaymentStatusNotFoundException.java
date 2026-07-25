package com.example.ecommerce_backend.modules.payment.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class PaymentStatusNotFoundException extends BaseException {
    public PaymentStatusNotFoundException(String code) {
        super("Payment status not found: " + code, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
