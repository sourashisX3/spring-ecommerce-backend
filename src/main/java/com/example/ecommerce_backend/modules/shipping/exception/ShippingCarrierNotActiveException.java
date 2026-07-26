package com.example.ecommerce_backend.modules.shipping.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ShippingCarrierNotActiveException extends BaseException {
    public ShippingCarrierNotActiveException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
