package com.example.ecommerce_backend.modules.tag.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class TagNotFoundException extends BaseException {

    public TagNotFoundException(String slug) {
        super("Tag not found with slug: " + slug, HttpStatus.NOT_FOUND);
    }
}
