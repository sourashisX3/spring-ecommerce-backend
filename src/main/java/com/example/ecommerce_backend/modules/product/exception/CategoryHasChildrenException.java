package com.example.ecommerce_backend.modules.product.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CategoryHasChildrenException extends BaseException {

    public CategoryHasChildrenException(String slug) {
        super("Category '" + slug + "' has child categories. Reassign or delete them first.", HttpStatus.BAD_REQUEST);
    }
}
