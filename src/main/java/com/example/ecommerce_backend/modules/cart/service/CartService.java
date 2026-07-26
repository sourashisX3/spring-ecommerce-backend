package com.example.ecommerce_backend.modules.cart.service;

import com.example.ecommerce_backend.modules.cart.dto.request.CartItemRequest;
import com.example.ecommerce_backend.modules.cart.dto.response.CartItemResponse;
import com.example.ecommerce_backend.modules.cart.entity.CartItem;
import com.example.ecommerce_backend.modules.cart.exception.CartItemNotFoundException;
import com.example.ecommerce_backend.modules.cart.mapper.CartMapper;
import com.example.ecommerce_backend.modules.cart.repository.CartRepository;
import com.example.ecommerce_backend.modules.product.entity.Product;
import com.example.ecommerce_backend.modules.product.exception.ProductNotFoundException;
import com.example.ecommerce_backend.modules.product.repository.ProductRepository;
import com.example.ecommerce_backend.modules.cart.exception.ProductNotActiveException;
import com.example.ecommerce_backend.modules.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<CartItemResponse> getCart(User user) {
        return cartRepository.findByUserId(user.getId()).stream()
                .map(CartMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CartItemResponse addToCart(String productUuid, CartItemRequest request, User user) {
        Product product = productRepository.findByUuid(productUuid)
                .orElseThrow(() -> new ProductNotFoundException(productUuid));

        if (!product.isActive()) {
            throw new ProductNotActiveException("Product is not available: " + product.getName());
        }

        CartItem item = cartRepository.findByUserIdAndProductId(user.getId(), product.getId())
                .orElseGet(() -> CartItem.builder()
                        .user(user)
                        .product(product)
                        .quantity(0)
                        .build());

        item.setQuantity(item.getQuantity() + request.getQuantity());
        item = cartRepository.save(item);
        return CartMapper.toResponse(item);
    }

    @Transactional
    public CartItemResponse updateQuantity(String itemUuid, CartItemRequest request, User user) {
        CartItem item = cartRepository.findByUuid(itemUuid)
                .orElseThrow(() -> new CartItemNotFoundException(itemUuid));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new CartItemNotFoundException(itemUuid);
        }

        item.setQuantity(request.getQuantity());
        item = cartRepository.save(item);
        return CartMapper.toResponse(item);
    }

    @Transactional
    public void removeFromCart(String itemUuid, User user) {
        CartItem item = cartRepository.findByUuid(itemUuid)
                .orElseThrow(() -> new CartItemNotFoundException(itemUuid));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new CartItemNotFoundException(itemUuid);
        }

        cartRepository.delete(item);
    }

    @Transactional
    public void clearCart(User user) {
        List<CartItem> items = cartRepository.findByUserId(user.getId());
        cartRepository.deleteAll(items);
    }
}
