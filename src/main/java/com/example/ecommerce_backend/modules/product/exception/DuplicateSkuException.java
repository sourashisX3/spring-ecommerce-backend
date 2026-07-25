package com.example.ecommerce_backend.modules.product.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DuplicateSkuException extends BaseException {

    public DuplicateSkuException(String sku) {
        super("SKU '" + sku + "' already exists", HttpStatus.CONFLICT);
    }
}
