package com.example.ecommerce_backend.modules.order.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class OrderStatusNotFoundException extends BaseException {
    public OrderStatusNotFoundException(String code) {
        super("Order status not found: " + code, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
