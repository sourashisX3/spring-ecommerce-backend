package com.example.ecommerce_backend.modules.cart.service;

import com.example.ecommerce_backend.modules.cart.dto.request.CartItemRequest;
import com.example.ecommerce_backend.modules.cart.dto.response.CartItemResponse;
import com.example.ecommerce_backend.modules.cart.entity.CartItem;
import com.example.ecommerce_backend.modules.cart.exception.CartItemNotFoundException;
import com.example.ecommerce_backend.modules.cart.exception.ProductNotActiveException;
import com.example.ecommerce_backend.modules.cart.repository.CartRepository;
import com.example.ecommerce_backend.modules.product.entity.Product;
import com.example.ecommerce_backend.modules.product.exception.ProductNotFoundException;
import com.example.ecommerce_backend.modules.product.repository.ProductRepository;
import com.example.ecommerce_backend.modules.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    private User user;
    private Product product;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).uuid("user-uuid").email("test@test.com").build();
        product = Product.builder()
                .id(1L).uuid("product-uuid")
                .name("Test Product")
                .basePrice(BigDecimal.valueOf(29.99))
                .isActive(true)
                .build();
        cartItem = CartItem.builder()
                .id(1L).uuid("cart-uuid")
                .user(user)
                .product(product)
                .quantity(2)
                .build();
    }

    @Test
    void getCart_shouldReturnItems() {
        when(cartRepository.findByUserId(1L)).thenReturn(List.of(cartItem));

        List<CartItemResponse> result = cartService.getCart(user);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProductUuid()).isEqualTo("product-uuid");
    }

    @Test
    void getCart_whenEmpty_shouldReturnEmptyList() {
        when(cartRepository.findByUserId(1L)).thenReturn(List.of());

        List<CartItemResponse> result = cartService.getCart(user);

        assertThat(result).isEmpty();
    }

    @Test
    void addToCart_shouldAddNewItem() {
        CartItemRequest request = new CartItemRequest();
        request.setQuantity(2);

        when(productRepository.findByUuid("product-uuid")).thenReturn(Optional.of(product));
        when(cartRepository.findByUserIdAndProductId(1L, 1L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(CartItem.class))).thenReturn(cartItem);

        CartItemResponse result = cartService.addToCart("product-uuid", request, user);

        assertThat(result.getProductUuid()).isEqualTo("product-uuid");
        assertThat(result.getQuantity()).isEqualTo(2);
    }

    @Test
    void addToCart_whenProductInactive_shouldThrow() {
        product.setActive(false);
        CartItemRequest request = new CartItemRequest();
        request.setQuantity(1);

        when(productRepository.findByUuid("product-uuid")).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> cartService.addToCart("product-uuid", request, user))
                .isInstanceOf(ProductNotActiveException.class);
    }

    @Test
    void addToCart_whenProductNotFound_shouldThrow() {
        CartItemRequest request = new CartItemRequest();
        request.setQuantity(1);

        when(productRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addToCart("nonexistent", request, user))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void addToCart_whenAlreadyInCart_shouldIncreaseQuantity() {
        CartItemRequest request = new CartItemRequest();
        request.setQuantity(3);

        when(productRepository.findByUuid("product-uuid")).thenReturn(Optional.of(product));
        when(cartRepository.findByUserIdAndProductId(1L, 1L)).thenReturn(Optional.of(cartItem));
        when(cartRepository.save(any(CartItem.class))).thenReturn(cartItem);

        CartItemResponse result = cartService.addToCart("product-uuid", request, user);

        assertThat(result.getQuantity()).isEqualTo(5);
    }

    @Test
    void updateQuantity_shouldUpdate() {
        CartItemRequest request = new CartItemRequest();
        request.setQuantity(5);

        when(cartRepository.findByUuid("cart-uuid")).thenReturn(Optional.of(cartItem));
        when(cartRepository.save(any(CartItem.class))).thenReturn(cartItem);

        CartItemResponse result = cartService.updateQuantity("cart-uuid", request, user);

        assertThat(result.getQuantity()).isEqualTo(5);
    }

    @Test
    void updateQuantity_whenNotOwnedByUser_shouldThrow() {
        User otherUser = User.builder().id(2L).uuid("other-uuid").build();
        CartItemRequest request = new CartItemRequest();
        request.setQuantity(3);

        when(cartRepository.findByUuid("cart-uuid")).thenReturn(Optional.of(cartItem));

        assertThatThrownBy(() -> cartService.updateQuantity("cart-uuid", request, otherUser))
                .isInstanceOf(CartItemNotFoundException.class);
    }

    @Test
    void updateQuantity_whenNotFound_shouldThrow() {
        CartItemRequest request = new CartItemRequest();
        request.setQuantity(3);

        when(cartRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.updateQuantity("nonexistent", request, user))
                .isInstanceOf(CartItemNotFoundException.class);
    }

    @Test
    void removeFromCart_shouldRemove() {
        when(cartRepository.findByUuid("cart-uuid")).thenReturn(Optional.of(cartItem));

        cartService.removeFromCart("cart-uuid", user);

        verify(cartRepository).delete(cartItem);
    }

    @Test
    void removeFromCart_whenNotOwnedByUser_shouldThrow() {
        User otherUser = User.builder().id(2L).uuid("other-uuid").build();

        when(cartRepository.findByUuid("cart-uuid")).thenReturn(Optional.of(cartItem));

        assertThatThrownBy(() -> cartService.removeFromCart("cart-uuid", otherUser))
                .isInstanceOf(CartItemNotFoundException.class);
    }

    @Test
    void clearCart_shouldClear() {
        when(cartRepository.findByUserId(1L)).thenReturn(List.of(cartItem));

        cartService.clearCart(user);

        verify(cartRepository).deleteAll(List.of(cartItem));
    }
}
