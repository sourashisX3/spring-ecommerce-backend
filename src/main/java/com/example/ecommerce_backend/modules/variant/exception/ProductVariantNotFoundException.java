package com.example.ecommerce_backend.modules.variant.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ProductVariantNotFoundException extends BaseException {

    public ProductVariantNotFoundException(Long id) {
        super("Product variant not found with id: " + id, HttpStatus.NOT_FOUND);
    }

    public ProductVariantNotFoundException(String uuid) {
        super("Product variant not found with uuid: " + uuid, HttpStatus.NOT_FOUND);
    }
}
