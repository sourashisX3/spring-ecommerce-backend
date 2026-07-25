package com.example.ecommerce_backend.modules.coupon.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CouponNotFoundException extends BaseException {

    public CouponNotFoundException(String uuid) {
        super("Coupon not found with uuid: " + uuid, HttpStatus.NOT_FOUND);
    }

    public CouponNotFoundException(String message, HttpStatus status) {
        super(message, status);
    }
}
