package com.example.ecommerce_backend.modules.shipping.repository;

import com.example.ecommerce_backend.modules.shipping.entity.ShippingCarrier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShippingCarrierRepository extends JpaRepository<ShippingCarrier, Long> {
    Optional<ShippingCarrier> findByUuid(String uuid);
    Optional<ShippingCarrier> findByCode(String code);
}
