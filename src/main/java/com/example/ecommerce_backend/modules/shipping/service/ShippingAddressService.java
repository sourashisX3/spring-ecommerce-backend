package com.example.ecommerce_backend.modules.shipping.service;

import com.example.ecommerce_backend.modules.shipping.dto.request.AddressRequest;
import com.example.ecommerce_backend.modules.shipping.dto.response.AddressResponse;
import com.example.ecommerce_backend.modules.shipping.entity.ShippingAddress;
import com.example.ecommerce_backend.modules.shipping.exception.AddressNotFoundException;
import com.example.ecommerce_backend.modules.shipping.mapper.ShippingAddressMapper;
import com.example.ecommerce_backend.modules.shipping.repository.ShippingAddressRepository;
import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.user.exception.UserNotFoundException;
import com.example.ecommerce_backend.modules.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShippingAddressService {

    @Autowired
    private ShippingAddressRepository shippingAddressRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<AddressResponse> getAddresses(Long userId) {
        return shippingAddressRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(ShippingAddressMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AddressResponse getByUuid(String uuid, Long userId) {
        ShippingAddress address = shippingAddressRepository.findByUuid(uuid)
                .orElseThrow(() -> new AddressNotFoundException(uuid));
        if (!address.getUser().getId().equals(userId)) {
            throw new AddressNotFoundException(uuid);
        }
        return ShippingAddressMapper.toResponse(address);
    }

    @Transactional
    public AddressResponse create(AddressRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("id: " + userId));

        if (request.isDefault()) {
            shippingAddressRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(existing -> {
                        existing.setDefault(false);
                        shippingAddressRepository.save(existing);
                    });
        }

        ShippingAddress address = ShippingAddress.builder()
                .user(user)
                .label(request.getLabel())
                .recipientName(request.getRecipientName())
                .phone(request.getPhone())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .isDefault(request.isDefault())
                .build();

        address = shippingAddressRepository.save(address);
        return ShippingAddressMapper.toResponse(address);
    }

    @Transactional
    public AddressResponse update(String uuid, AddressRequest request, Long userId) {
        ShippingAddress address = shippingAddressRepository.findByUuid(uuid)
                .orElseThrow(() -> new AddressNotFoundException(uuid));
        if (!address.getUser().getId().equals(userId)) {
            throw new AddressNotFoundException(uuid);
        }

        if (request.isDefault() && !address.isDefault()) {
            shippingAddressRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(existing -> {
                        existing.setDefault(false);
                        shippingAddressRepository.save(existing);
                    });
        }

        address.setLabel(request.getLabel());
        address.setRecipientName(request.getRecipientName());
        address.setPhone(request.getPhone());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());
        address.setDefault(request.isDefault());

        address = shippingAddressRepository.save(address);
        return ShippingAddressMapper.toResponse(address);
    }

    @Transactional
    public void setDefault(String uuid, Long userId) {
        ShippingAddress address = shippingAddressRepository.findByUuid(uuid)
                .orElseThrow(() -> new AddressNotFoundException(uuid));
        if (!address.getUser().getId().equals(userId)) {
            throw new AddressNotFoundException(uuid);
        }

        shippingAddressRepository.findByUserIdAndIsDefaultTrue(userId)
                .ifPresent(existing -> {
                    existing.setDefault(false);
                    shippingAddressRepository.save(existing);
                });

        address.setDefault(true);
        shippingAddressRepository.save(address);
    }

    @Transactional
    public void delete(String uuid, Long userId) {
        ShippingAddress address = shippingAddressRepository.findByUuid(uuid)
                .orElseThrow(() -> new AddressNotFoundException(uuid));
        if (!address.getUser().getId().equals(userId)) {
            throw new AddressNotFoundException(uuid);
        }
        shippingAddressRepository.delete(address);
    }
}
