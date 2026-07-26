package com.example.ecommerce_backend.modules.payment.service;

import com.example.ecommerce_backend.modules.payment.entity.PaymentStatus;
import com.example.ecommerce_backend.modules.payment.exception.PaymentStatusNotFoundException;
import com.example.ecommerce_backend.modules.payment.repository.PaymentStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentStatusService {

    @Autowired
    private PaymentStatusRepository paymentStatusRepository;

    @Transactional
    public boolean toggleStatus(String uuid, boolean isActive) {
        PaymentStatus status = paymentStatusRepository.findByUuid(uuid)
                .orElseThrow(() -> new PaymentStatusNotFoundException("Payment status not found: " + uuid));
        if (status.isActive() == isActive) {
            return false;
        }
        status.setActive(isActive);
        paymentStatusRepository.save(status);
        return true;
    }
}
