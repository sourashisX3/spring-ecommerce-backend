package com.example.ecommerce_backend.modules.product.service;

import com.example.ecommerce_backend.modules.product.dto.response.WishlistItemResponse;
import com.example.ecommerce_backend.modules.product.entity.Product;
import com.example.ecommerce_backend.modules.product.entity.WishlistItem;
import com.example.ecommerce_backend.modules.product.exception.DuplicateWishlistItemException;
import com.example.ecommerce_backend.modules.product.exception.ProductNotFoundException;
import com.example.ecommerce_backend.modules.product.exception.WishlistItemNotFoundException;
import com.example.ecommerce_backend.modules.product.mapper.WishlistMapper;
import com.example.ecommerce_backend.modules.product.repository.ProductRepository;
import com.example.ecommerce_backend.modules.product.repository.WishlistRepository;
import com.example.ecommerce_backend.modules.role_user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<WishlistItemResponse> getWishlist(User user) {
        return wishlistRepository.findByUserId(user.getId()).stream()
                .map(WishlistMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public WishlistItemResponse addToWishlist(String productUuid, User user) {
        Product product = productRepository.findByUuid(productUuid)
                .orElseThrow(() -> new ProductNotFoundException(productUuid));

        if (wishlistRepository.existsByUserIdAndProductId(user.getId(), product.getId())) {
            throw new DuplicateWishlistItemException();
        }

        WishlistItem item = WishlistItem.builder()
                .user(user)
                .product(product)
                .build();

        item = wishlistRepository.save(item);
        return WishlistMapper.toResponse(item);
    }

    @Transactional
    public void removeFromWishlist(String itemUuid, User user) {
        WishlistItem item = wishlistRepository.findByUuid(itemUuid)
                .orElseThrow(() -> new WishlistItemNotFoundException(itemUuid));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new WishlistItemNotFoundException(itemUuid);
        }

        wishlistRepository.delete(item);
    }
}