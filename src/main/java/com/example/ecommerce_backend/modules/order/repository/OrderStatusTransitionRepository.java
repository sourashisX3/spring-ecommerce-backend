package com.example.ecommerce_backend.modules.order.repository;

import com.example.ecommerce_backend.modules.order.entity.OrderStatusTransition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderStatusTransitionRepository extends JpaRepository<OrderStatusTransition, Long> {

    List<OrderStatusTransition> findByFromStatus_CodeAndAllowedBy_RoleName(String statusCode, String allowedByRoleName);

    List<OrderStatusTransition> findByFromStatus_IdAndAllowedBy_RoleName(Long fromStatusId, String allowedByRoleName);

    Optional<OrderStatusTransition> findByFromStatus_CodeAndToStatus_CodeAndAllowedBy_RoleName(
            String fromCode, String toCode, String allowedByRoleName);
}
