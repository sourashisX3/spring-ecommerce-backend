package com.example.ecommerce_backend.modules.shipping.service;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.core.exception.BaseException;
import com.example.ecommerce_backend.modules.shipping.dto.request.ShippingCarrierRequest;
import com.example.ecommerce_backend.modules.shipping.dto.response.ShippingCarrierResponse;
import com.example.ecommerce_backend.modules.shipping.entity.ShippingCarrier;
import com.example.ecommerce_backend.modules.shipping.repository.ShippingCarrierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShippingCarrierService {

    @Autowired
    private ShippingCarrierRepository shippingCarrierRepository;

    @Transactional(readOnly = true)
    public List<ShippingCarrierResponse> getAll() {
        return shippingCarrierRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ShippingCarrierResponse getByUuid(String uuid) {
        ShippingCarrier carrier = shippingCarrierRepository.findByUuid(uuid)
                .orElseThrow(() -> new BaseException("Shipping carrier not found: " + uuid, HttpStatus.NOT_FOUND));
        return toResponse(carrier);
    }

    @Transactional
    @RequiresPermission("shipping:write")
    public ShippingCarrierResponse create(ShippingCarrierRequest request) {
        if (shippingCarrierRepository.findByCode(request.getCode()).isPresent()) {
            throw new BaseException("Shipping carrier code already exists: " + request.getCode(), HttpStatus.CONFLICT);
        }
        ShippingCarrier carrier = ShippingCarrier.builder()
                .code(request.getCode())
                .name(request.getName())
                .trackingUrlTemplate(request.getTrackingUrlTemplate())
                .build();
        carrier = shippingCarrierRepository.save(carrier);
        return toResponse(carrier);
    }

    @Transactional
    @RequiresPermission("shipping:write")
    public ShippingCarrierResponse update(String uuid, ShippingCarrierRequest request) {
        ShippingCarrier carrier = shippingCarrierRepository.findByUuid(uuid)
                .orElseThrow(() -> new BaseException("Shipping carrier not found: " + uuid, HttpStatus.NOT_FOUND));
        carrier.setCode(request.getCode());
        carrier.setName(request.getName());
        carrier.setTrackingUrlTemplate(request.getTrackingUrlTemplate());
        carrier = shippingCarrierRepository.save(carrier);
        return toResponse(carrier);
    }

    @Transactional
    @RequiresPermission("shipping:write")
    public ShippingCarrierResponse toggleStatus(String uuid) {
        ShippingCarrier carrier = shippingCarrierRepository.findByUuid(uuid)
                .orElseThrow(() -> new BaseException("Shipping carrier not found: " + uuid, HttpStatus.NOT_FOUND));
        carrier.setActive(!carrier.isActive());
        carrier = shippingCarrierRepository.save(carrier);
        return toResponse(carrier);
    }

    @Transactional
    @RequiresPermission("shipping:write")
    public void delete(String uuid) {
        ShippingCarrier carrier = shippingCarrierRepository.findByUuid(uuid)
                .orElseThrow(() -> new BaseException("Shipping carrier not found: " + uuid, HttpStatus.NOT_FOUND));
        shippingCarrierRepository.delete(carrier);
    }

    private ShippingCarrierResponse toResponse(ShippingCarrier carrier) {
        return ShippingCarrierResponse.builder()
                .id(carrier.getId())
                .uuid(carrier.getUuid())
                .code(carrier.getCode())
                .name(carrier.getName())
                .trackingUrlTemplate(carrier.getTrackingUrlTemplate())
                .isActive(carrier.isActive())
                .createdAt(carrier.getCreatedAt())
                .updatedAt(carrier.getUpdatedAt())
                .build();
    }
}
