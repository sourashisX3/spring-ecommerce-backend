package com.example.ecommerce_backend.modules.product.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends BaseException {

    public ProductNotFoundException(String uuid) {
        super("Product not found with uuid: " + uuid, HttpStatus.NOT_FOUND);
    }
}
