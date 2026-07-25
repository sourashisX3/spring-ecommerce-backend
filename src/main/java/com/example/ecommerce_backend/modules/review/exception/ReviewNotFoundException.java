package com.example.ecommerce_backend.modules.review.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ReviewNotFoundException extends BaseException {

    public ReviewNotFoundException(String uuid) {
        super("Review not found with uuid: " + uuid, HttpStatus.NOT_FOUND);
    }
}
