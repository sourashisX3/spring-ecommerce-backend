package com.example.ecommerce_backend.modules.product.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CartItemNotFoundException extends BaseException {

    public CartItemNotFoundException(String uuid) {
        super("Cart item not found with uuid: " + uuid, HttpStatus.NOT_FOUND);
    }
}