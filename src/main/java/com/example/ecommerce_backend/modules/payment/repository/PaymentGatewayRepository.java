package com.example.ecommerce_backend.modules.payment.repository;

import com.example.ecommerce_backend.modules.payment.entity.PaymentGateway;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentGatewayRepository extends JpaRepository<PaymentGateway, Long> {
    Optional<PaymentGateway> findByUuid(String uuid);
    Optional<PaymentGateway> findByCode(String code);
    boolean existsByCode(String code);
}
