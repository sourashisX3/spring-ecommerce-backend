package com.example.ecommerce_backend.modules.returns.repository;

import com.example.ecommerce_backend.modules.returns.entity.ReturnType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReturnTypeRepository extends JpaRepository<ReturnType, Long> {
    Optional<ReturnType> findByUuid(String uuid);
    Optional<ReturnType> findByCode(String code);
}
