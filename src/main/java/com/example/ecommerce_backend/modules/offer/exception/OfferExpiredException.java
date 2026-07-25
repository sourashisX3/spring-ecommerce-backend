package com.example.ecommerce_backend.modules.offer.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class OfferExpiredException extends BaseException {

    public OfferExpiredException(String uuid) {
        super("Offer has expired: " + uuid, HttpStatus.BAD_REQUEST);
    }
}
