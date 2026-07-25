package com.example.ecommerce_backend.modules.order.repository;

import com.example.ecommerce_backend.modules.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {

    Optional<OrderStatus> findByUuid(String uuid);

    Optional<OrderStatus> findByCode(String code);

    List<OrderStatus> findAllByOrderBySortOrderAsc();
}
