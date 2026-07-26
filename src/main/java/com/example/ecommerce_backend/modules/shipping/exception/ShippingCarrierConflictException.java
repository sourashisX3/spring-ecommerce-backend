package com.example.ecommerce_backend.modules.shipping.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ShippingCarrierConflictException extends BaseException {
    public ShippingCarrierConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
