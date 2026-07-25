package com.example.ecommerce_backend.modules.payment.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RefundStatusNotFoundException extends BaseException {
    public RefundStatusNotFoundException(String code) {
        super("Refund status not found: " + code, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
