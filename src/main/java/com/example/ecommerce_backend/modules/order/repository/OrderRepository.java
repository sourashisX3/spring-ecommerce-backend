package com.example.ecommerce_backend.modules.order.repository;

import com.example.ecommerce_backend.modules.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByUuid(String uuid);

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByUserId(Long userId);

    Page<Order> findByUserId(Long userId, Pageable pageable);

    boolean existsByOrderNumber(String orderNumber);

    long countByUserId(Long userId);

    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE o.status.code NOT IN ('CANCELLED', 'REFUNDED')")
    BigDecimal sumRevenue();

    @Query("SELECT o FROM Order o WHERE o.status.code NOT IN ('CANCELLED', 'REFUNDED') AND o.createdAt >= :from")
    List<Order> findRevenueSince(@Param("from") Instant from);

    @Query("SELECT o.status.name, COUNT(o) FROM Order o GROUP BY o.status.name")
    List<Object[]> countByStatusName();
}