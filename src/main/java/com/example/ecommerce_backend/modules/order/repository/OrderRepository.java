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

    @Query("""
            SELECT o FROM Order o
            LEFT JOIN o.user u
            WHERE (:search IS NULL OR LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:status IS NULL OR o.status.code = :status)
              AND (:from IS NULL OR o.createdAt >= :from)
              AND (:to IS NULL OR o.createdAt <= :to)
            """)
    Page<Order> search(@Param("search") String search, @Param("status") String status,
                       @Param("from") Instant from, @Param("to") Instant to, Pageable pageable);
}