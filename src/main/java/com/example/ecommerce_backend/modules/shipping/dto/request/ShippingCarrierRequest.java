package com.example.ecommerce_backend.modules.shipping.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShippingCarrierRequest {
    @NotBlank
    private String code;
    @NotBlank
    private String name;
    private String trackingUrlTemplate;
}
