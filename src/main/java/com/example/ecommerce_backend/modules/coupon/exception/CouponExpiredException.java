package com.example.ecommerce_backend.modules.coupon.exception;

import com.example.ecommerce_backend.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CouponExpiredException extends BaseException {

    public CouponExpiredException(String code) {
        super("Coupon has expired or is inactive: " + code, HttpStatus.BAD_REQUEST);
    }

    public CouponExpiredException(String message, HttpStatus status) {
        super(message, status);
    }
}
