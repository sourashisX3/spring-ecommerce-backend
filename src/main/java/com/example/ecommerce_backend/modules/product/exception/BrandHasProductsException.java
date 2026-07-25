package com.example.ecommerce_backend.modules.product.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class BrandHasProductsException extends BaseException {

    public BrandHasProductsException(String slug) {
        super("Brand '" + slug + "' has products. Reassign them first.", HttpStatus.BAD_REQUEST);
    }
}
