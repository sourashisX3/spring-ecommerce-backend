package com.example.ecommerce_backend.modules.cart.repository;

import com.example.ecommerce_backend.modules.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserId(Long userId);

    void deleteByUserId(Long userId);

    Optional<CartItem> findByUuid(String uuid);

    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);
}
