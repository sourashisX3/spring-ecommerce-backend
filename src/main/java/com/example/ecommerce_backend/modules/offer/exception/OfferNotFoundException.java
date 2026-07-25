package com.example.ecommerce_backend.modules.offer.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class OfferNotFoundException extends BaseException {

    public OfferNotFoundException(String uuid) {
        super("Offer not found with uuid: " + uuid, HttpStatus.NOT_FOUND);
    }
}
