package com.example.ecommerce_backend.modules.shipping.repository;

import com.example.ecommerce_backend.modules.shipping.entity.ShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {

    Optional<ShippingAddress> findByUuid(String uuid);

    List<ShippingAddress> findByUserId(Long userId);

    void deleteByUserId(Long userId);

    Optional<ShippingAddress> findByUserIdAndIsDefaultTrue(Long userId);

    List<ShippingAddress> findByUserIdOrderByCreatedAtDesc(Long userId);
}
