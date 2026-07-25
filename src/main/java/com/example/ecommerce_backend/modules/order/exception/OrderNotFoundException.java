package com.example.ecommerce_backend.modules.order.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends BaseException {

    public OrderNotFoundException(String uuid) {
        super("Order not found with uuid: " + uuid, HttpStatus.NOT_FOUND);
    }
}
