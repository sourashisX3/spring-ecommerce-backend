package com.example.ecommerce_backend.modules.product.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class BrandNotFoundException extends BaseException {

    public BrandNotFoundException(String slug) {
        super("Brand not found with slug: " + slug, HttpStatus.NOT_FOUND);
    }
}
