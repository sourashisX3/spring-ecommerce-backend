package com.example.ecommerce_backend.modules.banner.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class BannerNotFoundException extends BaseException {

    public BannerNotFoundException(String uuid) {
        super("Banner not found with uuid: " + uuid, HttpStatus.NOT_FOUND);
    }
}