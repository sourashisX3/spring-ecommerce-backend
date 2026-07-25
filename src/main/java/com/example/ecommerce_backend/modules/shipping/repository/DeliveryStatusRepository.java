package com.example.ecommerce_backend.modules.shipping.repository;

import com.example.ecommerce_backend.modules.shipping.entity.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryStatusRepository extends JpaRepository<DeliveryStatus, Long> {

    Optional<DeliveryStatus> findByUuid(String uuid);

    Optional<DeliveryStatus> findByCode(String code);
}
