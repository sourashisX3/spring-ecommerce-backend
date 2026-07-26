package com.example.ecommerce_backend.modules.payment.service;

import com.example.ecommerce_backend.modules.payment.entity.RefundStatus;
import com.example.ecommerce_backend.modules.payment.exception.RefundStatusNotFoundException;
import com.example.ecommerce_backend.modules.payment.repository.RefundStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefundStatusService {

    @Autowired
    private RefundStatusRepository refundStatusRepository;

    @Transactional
    public boolean toggleStatus(String uuid, boolean isActive) {
        RefundStatus status = refundStatusRepository.findByUuid(uuid)
                .orElseThrow(() -> new RefundStatusNotFoundException("Refund status not found: " + uuid));
        if (status.isActive() == isActive) {
            return false;
        }
        status.setActive(isActive);
        refundStatusRepository.save(status);
        return true;
    }
}
