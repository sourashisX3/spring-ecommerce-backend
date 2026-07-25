package com.example.ecommerce_backend.modules.product.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends BaseException {

    public CategoryNotFoundException(String slug) {
        super("Category not found with slug: " + slug, HttpStatus.NOT_FOUND);
    }
}
