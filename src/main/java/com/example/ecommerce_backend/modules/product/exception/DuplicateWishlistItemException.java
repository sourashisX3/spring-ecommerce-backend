package com.example.ecommerce_backend.modules.product.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DuplicateWishlistItemException extends BaseException {

    public DuplicateWishlistItemException() {
        super("Product is already in your wishlist", HttpStatus.CONFLICT);
    }
}