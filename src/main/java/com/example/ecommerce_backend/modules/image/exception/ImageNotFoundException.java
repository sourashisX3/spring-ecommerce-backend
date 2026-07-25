package com.example.ecommerce_backend.modules.image.exception;

public class ImageNotFoundException extends RuntimeException {

    public ImageNotFoundException(String uuid) {
        super("Image not found with uuid: " + uuid);
    }
}
