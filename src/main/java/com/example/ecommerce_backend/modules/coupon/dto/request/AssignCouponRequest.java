package com.example.ecommerce_backend.modules.coupon.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AssignCouponRequest {

    @NotEmpty(message = "User UUIDs are required")
    private List<String> userUuids;
}
