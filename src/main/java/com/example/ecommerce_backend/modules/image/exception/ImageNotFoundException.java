package com.example.ecommerce_backend.modules.image.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ImageNotFoundException extends BaseException {

    public ImageNotFoundException(String uuid) {
        super("Image not found with uuid: " + uuid, HttpStatus.NOT_FOUND);
    }
}
