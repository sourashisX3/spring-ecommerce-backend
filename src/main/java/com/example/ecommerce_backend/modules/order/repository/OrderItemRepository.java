package com.example.ecommerce_backend.modules.order.repository;

import com.example.ecommerce_backend.modules.order.entity.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    @Query("""
            SELECT oi.productId, oi.productName, oi.sku, SUM(oi.quantity), SUM(oi.totalPrice)
            FROM OrderItem oi
            WHERE oi.order.status.code NOT IN ('CANCELLED', 'REFUNDED')
            GROUP BY oi.productId, oi.productName, oi.sku
            ORDER BY SUM(oi.quantity) DESC
            """)
    List<Object[]> findTopProducts(Pageable pageable);
}