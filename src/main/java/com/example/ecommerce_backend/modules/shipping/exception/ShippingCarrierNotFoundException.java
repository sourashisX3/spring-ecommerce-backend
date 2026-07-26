package com.example.ecommerce_backend.modules.shipping.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ShippingCarrierNotFoundException extends BaseException {
    public ShippingCarrierNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
