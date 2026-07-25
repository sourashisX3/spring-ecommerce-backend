package com.example.ecommerce_backend.modules.discount.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DiscountNotFoundException extends BaseException {
    public DiscountNotFoundException(String identifier) {
        super("Discount not found: " + identifier, HttpStatus.NOT_FOUND);
    }
}
