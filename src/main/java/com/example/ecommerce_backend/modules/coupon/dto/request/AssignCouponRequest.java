package com.example.ecommerce_backend.modules.coupon.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Request to assign a coupon to specific users")
public class AssignCouponRequest {

    @NotEmpty(message = "User UUIDs are required")
    @Schema(description = "List of user UUIDs to assign the coupon to", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> userUuids;
}
