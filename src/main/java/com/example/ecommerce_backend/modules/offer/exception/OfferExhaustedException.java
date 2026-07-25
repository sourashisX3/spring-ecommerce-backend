package com.example.ecommerce_backend.modules.offer.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class OfferExhaustedException extends BaseException {

    public OfferExhaustedException(String uuid) {
        super("Offer usage limit reached: " + uuid, HttpStatus.BAD_REQUEST);
    }
}
