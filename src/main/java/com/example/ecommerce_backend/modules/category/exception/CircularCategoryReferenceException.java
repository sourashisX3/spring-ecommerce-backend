package com.example.ecommerce_backend.modules.category.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CircularCategoryReferenceException extends BaseException {

    public CircularCategoryReferenceException() {
        super("Cannot set category as its own descendant", HttpStatus.BAD_REQUEST);
    }
}
