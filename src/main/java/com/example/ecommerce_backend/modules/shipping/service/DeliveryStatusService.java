package com.example.ecommerce_backend.modules.shipping.service;

import com.example.ecommerce_backend.modules.shipping.entity.DeliveryStatus;
import com.example.ecommerce_backend.modules.shipping.exception.DeliveryStatusNotFoundException;
import com.example.ecommerce_backend.modules.shipping.repository.DeliveryStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeliveryStatusService {

    @Autowired
    private DeliveryStatusRepository deliveryStatusRepository;

    @Transactional
    public boolean toggleStatus(String uuid, boolean isActive) {
        DeliveryStatus status = deliveryStatusRepository.findByUuid(uuid)
                .orElseThrow(() -> new DeliveryStatusNotFoundException("Delivery status not found: " + uuid));
        if (status.isActive() == isActive) {
            return false;
        }
        status.setActive(isActive);
        deliveryStatusRepository.save(status);
        return true;
    }
}
