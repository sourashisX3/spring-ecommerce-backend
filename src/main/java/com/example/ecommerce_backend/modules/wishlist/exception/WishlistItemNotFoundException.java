package com.example.ecommerce_backend.modules.wishlist.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class WishlistItemNotFoundException extends BaseException {

    public WishlistItemNotFoundException(String uuid) {
        super("Wishlist item not found with uuid: " + uuid, HttpStatus.NOT_FOUND);
    }
}
