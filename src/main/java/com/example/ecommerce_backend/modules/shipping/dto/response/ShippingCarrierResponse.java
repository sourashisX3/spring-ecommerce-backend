package com.example.ecommerce_backend.modules.shipping.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ShippingCarrierResponse {
    private Long id;
    private String uuid;
    private String code;
    private String name;
    private String trackingUrlTemplate;
    @JsonProperty("isActive")
    private boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}
