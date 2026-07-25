package com.example.ecommerce_backend.modules.returns.repository;

import com.example.ecommerce_backend.modules.returns.entity.ReturnCondition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReturnConditionRepository extends JpaRepository<ReturnCondition, Long> {
    Optional<ReturnCondition> findByUuid(String uuid);
    Optional<ReturnCondition> findByCode(String code);
}
