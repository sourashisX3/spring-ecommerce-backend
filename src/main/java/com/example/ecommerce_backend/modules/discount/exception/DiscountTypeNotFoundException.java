package com.example.ecommerce_backend.modules.discount.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DiscountTypeNotFoundException extends BaseException {
    public DiscountTypeNotFoundException(String identifier) {
        super("Discount type not found: " + identifier, HttpStatus.NOT_FOUND);
    }
}
