package com.example.ecommerce_backend.modules.offer.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AssignOfferRequest {

    @NotEmpty(message = "User UUIDs are required")
    private List<String> userUuids;

    private Integer usageLimitPerUser;
}
