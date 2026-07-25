package com.example.ecommerce_backend.modules.shipping.mapper;

import com.example.ecommerce_backend.modules.shipping.dto.response.AddressResponse;
import com.example.ecommerce_backend.modules.shipping.entity.ShippingAddress;

public class ShippingAddressMapper {

    private ShippingAddressMapper() {
    }

    public static AddressResponse toResponse(ShippingAddress address) {
        return AddressResponse.builder()
                .id(address.getId())
                .uuid(address.getUuid())
                .label(address.getLabel())
                .recipientName(address.getRecipientName())
                .phone(address.getPhone())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .isDefault(address.isDefault())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }
}
