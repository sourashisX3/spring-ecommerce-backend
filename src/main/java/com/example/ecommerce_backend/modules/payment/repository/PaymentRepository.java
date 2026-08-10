package com.example.ecommerce_backend.modules.payment.repository;

import com.example.ecommerce_backend.modules.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByUuid(String uuid);
    Optional<Payment> findByOrderId(Long orderId);
    List<Payment> findByUserId(Long userId);

    Page<Payment> findByUserId(Long userId, Pageable pageable);

    long countByUserId(Long userId);
}
