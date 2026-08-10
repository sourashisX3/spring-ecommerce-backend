package com.example.ecommerce_backend.modules.wishlist.repository;

import com.example.ecommerce_backend.modules.wishlist.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<WishlistItem, Long> {

    List<WishlistItem> findByUserId(Long userId);

    void deleteByUserId(Long userId);

    Optional<WishlistItem> findByUuid(String uuid);

    boolean existsByUserIdAndProductId(Long userId, Long productId);
}
