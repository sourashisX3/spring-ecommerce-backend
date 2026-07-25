package com.example.ecommerce_backend.modules.shipping.service;

import com.example.ecommerce_backend.core.event.DeliveryStatusChangedEvent;
import com.example.ecommerce_backend.core.exception.BaseException;
import com.example.ecommerce_backend.modules.shipping.dto.request.DeliveryRequest;
import com.example.ecommerce_backend.modules.shipping.dto.request.UpdateDeliveryRequest;
import com.example.ecommerce_backend.modules.shipping.dto.response.DeliveryResponse;
import com.example.ecommerce_backend.modules.shipping.entity.Delivery;
import com.example.ecommerce_backend.modules.shipping.entity.ShippingAddress;
import com.example.ecommerce_backend.modules.shipping.entity.ShippingCarrier;
import com.example.ecommerce_backend.modules.shipping.exception.AddressNotFoundException;
import com.example.ecommerce_backend.modules.shipping.exception.DeliveryNotFoundException;
import com.example.ecommerce_backend.modules.shipping.mapper.DeliveryMapper;
import com.example.ecommerce_backend.modules.shipping.entity.DeliveryStatus;
import com.example.ecommerce_backend.modules.shipping.repository.DeliveryRepository;
import com.example.ecommerce_backend.modules.shipping.repository.DeliveryStatusRepository;
import com.example.ecommerce_backend.modules.shipping.repository.ShippingAddressRepository;
import com.example.ecommerce_backend.modules.shipping.repository.ShippingCarrierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private ShippingAddressRepository shippingAddressRepository;

    @Autowired
    private ShippingCarrierRepository shippingCarrierRepository;

    @Autowired
    private DeliveryStatusRepository deliveryStatusRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public List<DeliveryResponse> getByOrderId(Long orderId) {
        return deliveryRepository.findByOrderId(orderId).stream()
                .map(DeliveryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DeliveryResponse createDelivery(Long orderId, DeliveryRequest request) {
        ShippingAddress address = shippingAddressRepository.findById(request.getShippingAddressId())
                .orElseThrow(() -> new AddressNotFoundException("id: " + request.getShippingAddressId()));

        ShippingCarrier carrier = shippingCarrierRepository.findByCode(request.getCarrierCode())
                .orElseThrow(() -> new BaseException("Shipping carrier not found: " + request.getCarrierCode(), HttpStatus.NOT_FOUND));

        DeliveryStatus pendingStatus = deliveryStatusRepository.findByCode("PENDING")
                .orElseThrow(() -> new BaseException("Delivery status not found: PENDING", HttpStatus.INTERNAL_SERVER_ERROR));

        Delivery delivery = Delivery.builder()
                .orderId(orderId)
                .shippingAddress(address)
                .carrier(carrier)
                .status(pendingStatus)
                .trackingNumber(request.getTrackingNumber())
                .estimatedDelivery(request.getEstimatedDelivery())
                .build();

        delivery = deliveryRepository.save(delivery);
        return DeliveryMapper.toResponse(delivery);
    }

    @Transactional
    public DeliveryResponse updateDelivery(String uuid, UpdateDeliveryRequest request) {
        Delivery delivery = deliveryRepository.findByUuid(uuid)
                .orElseThrow(() -> new DeliveryNotFoundException(uuid));

        if (request.getCarrierCode() != null) {
            ShippingCarrier carrier = shippingCarrierRepository.findByCode(request.getCarrierCode())
                    .orElseThrow(() -> new BaseException("Shipping carrier not found: " + request.getCarrierCode(), HttpStatus.NOT_FOUND));
            delivery.setCarrier(carrier);
        }
        if (request.getTrackingNumber() != null) {
            delivery.setTrackingNumber(request.getTrackingNumber());
        }
        if (request.getStatus() != null) {
            DeliveryStatus deliveryStatus = deliveryStatusRepository.findByCode(request.getStatus())
                    .orElseThrow(() -> new BaseException("Delivery status not found: " + request.getStatus(), HttpStatus.NOT_FOUND));
            delivery.setStatus(deliveryStatus);
            if ("SHIPPED".equals(request.getStatus()) && delivery.getShippedAt() == null) {
                delivery.setShippedAt(Instant.now());
            }
            if ("DELIVERED".equals(request.getStatus()) && delivery.getDeliveredAt() == null) {
                delivery.setDeliveredAt(Instant.now());
            }
        }
        if (request.getNotes() != null) {
            delivery.setNotes(request.getNotes());
        }
        if (request.getEstimatedDelivery() != null) {
            delivery.setEstimatedDelivery(request.getEstimatedDelivery());
        }

        delivery = deliveryRepository.save(delivery);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = authentication != null && authentication.getPrincipal() instanceof com.example.ecommerce_backend.modules.user.entity.User user
                ? user.getId() : null;
        eventPublisher.publishEvent(new DeliveryStatusChangedEvent(this, userId, delivery.getUuid(),
                request.getStatus()));
        return DeliveryMapper.toResponse(delivery);
    }
}
