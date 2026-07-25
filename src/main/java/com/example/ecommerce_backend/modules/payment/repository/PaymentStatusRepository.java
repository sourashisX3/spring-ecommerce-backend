package com.example.ecommerce_backend.modules.payment.repository;

import com.example.ecommerce_backend.modules.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentStatusRepository extends JpaRepository<PaymentStatus, Long> {

    Optional<PaymentStatus> findByUuid(String uuid);

    Optional<PaymentStatus> findByCode(String code);
}
