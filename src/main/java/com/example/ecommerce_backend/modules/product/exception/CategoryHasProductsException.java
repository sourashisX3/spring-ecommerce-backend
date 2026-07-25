package com.example.ecommerce_backend.modules.product.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CategoryHasProductsException extends BaseException {

    public CategoryHasProductsException(String slug) {
        super("Category '" + slug + "' has products. Reassign them first.", HttpStatus.BAD_REQUEST);
    }
}
