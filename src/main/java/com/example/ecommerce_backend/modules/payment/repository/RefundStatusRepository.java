package com.example.ecommerce_backend.modules.payment.repository;

import com.example.ecommerce_backend.modules.payment.entity.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefundStatusRepository extends JpaRepository<RefundStatus, Long> {

    Optional<RefundStatus> findByUuid(String uuid);

    Optional<RefundStatus> findByCode(String code);
}
