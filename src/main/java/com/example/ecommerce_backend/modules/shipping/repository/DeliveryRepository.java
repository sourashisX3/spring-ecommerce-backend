package com.example.ecommerce_backend.modules.shipping.repository;

import com.example.ecommerce_backend.modules.shipping.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Optional<Delivery> findByUuid(String uuid);

    List<Delivery> findByOrderId(Long orderId);
}
