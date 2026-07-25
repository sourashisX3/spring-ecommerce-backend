package com.example.ecommerce_backend.modules.order.service;

import com.example.ecommerce_backend.core.event.OrderCreatedEvent;
import com.example.ecommerce_backend.core.event.OrderStatusChangedEvent;
import com.example.ecommerce_backend.modules.cart.entity.CartItem;
import com.example.ecommerce_backend.modules.cart.repository.CartRepository;
import com.example.ecommerce_backend.modules.cart.service.CartService;
import com.example.ecommerce_backend.modules.coupon.service.CouponService;
import com.example.ecommerce_backend.modules.currency.entity.Currency;
import com.example.ecommerce_backend.modules.currency.repository.CurrencyRepository;
import com.example.ecommerce_backend.modules.order.dto.request.OrderRequest;
import com.example.ecommerce_backend.modules.order.dto.request.UpdateOrderStatusRequest;
import com.example.ecommerce_backend.modules.order.dto.response.OrderResponse;
import com.example.ecommerce_backend.modules.order.entity.Order;
import com.example.ecommerce_backend.modules.order.entity.OrderItem;
import com.example.ecommerce_backend.modules.order.entity.OrderStatus;
import com.example.ecommerce_backend.modules.order.entity.OrderStatusHistory;
import com.example.ecommerce_backend.modules.order.exception.InsufficientStockException;
import com.example.ecommerce_backend.modules.order.exception.InvalidOrderStateException;
import com.example.ecommerce_backend.modules.order.exception.OrderNotFoundException;
import com.example.ecommerce_backend.modules.order.repository.OrderRepository;
import com.example.ecommerce_backend.modules.order.repository.OrderStatusRepository;
import com.example.ecommerce_backend.modules.product.entity.Product;
import org.springframework.data.domain.Pageable;
import com.example.ecommerce_backend.modules.product.exception.ProductNotFoundException;
import com.example.ecommerce_backend.modules.product.repository.ProductRepository;
import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.user.exception.UserNotFoundException;
import com.example.ecommerce_backend.modules.user.repository.UserRepository;
import com.example.ecommerce_backend.modules.variant.repository.ProductVariantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartService cartService;

    @Mock
    private CouponService couponService;

    @Mock
    private OrderStatusRepository orderStatusRepository;

    @Mock
    private OrderStatusService orderStatusService;

    @Mock
    private ProductVariantRepository variantRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private OrderService orderService;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    private User user;
    private Currency usdCurrency;
    private Product activeProduct;
    private Product inactiveProduct;
    private OrderStatus pendingStatus;
    private OrderStatus cancelledStatus;
    private OrderStatus confirmedStatus;
    private CartItem cartItem;
    private Order order;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("test@example.com").firstName("Test").lastName("User").password("pass").isActive(true).build();

        usdCurrency = Currency.builder().id(1L).code("USD").name("US Dollar").symbol("$").build();

        activeProduct = Product.builder()
                .id(1L).uuid("prod-active").sku("SKU-A").name("Active Product")
                .basePrice(BigDecimal.valueOf(50)).isActive(true)
                .variants(new ArrayList<>()).images(new ArrayList<>()).tags(new java.util.HashSet<>())
                .build();

        inactiveProduct = Product.builder()
                .id(2L).uuid("prod-inactive").sku("SKU-I").name("Inactive Product")
                .basePrice(BigDecimal.valueOf(30)).isActive(false)
                .variants(new ArrayList<>()).images(new ArrayList<>()).tags(new java.util.HashSet<>())
                .build();

        pendingStatus = OrderStatus.builder().id(1L).code("PENDING").name("Pending").sortOrder(1).isActive(true).build();
        cancelledStatus = OrderStatus.builder().id(2L).code("CANCELLED").name("Cancelled").sortOrder(5).isActive(true).build();
        confirmedStatus = OrderStatus.builder().id(3L).code("CONFIRMED").name("Confirmed").sortOrder(2).isActive(true).build();

        cartItem = CartItem.builder().id(1L).user(user).product(activeProduct).quantity(2).build();

        order = Order.builder()
                .id(1L).uuid("order-uuid-1").orderNumber("ORD-ABC123")
                .user(user).status(pendingStatus)
                .subtotal(BigDecimal.valueOf(100)).discount(BigDecimal.ZERO)
                .shippingCost(BigDecimal.ZERO).tax(BigDecimal.ZERO)
                .total(BigDecimal.valueOf(100))
                .currency(usdCurrency)
                .items(new ArrayList<>()).statusHistory(new ArrayList<>())
                .createdAt(Instant.now()).updatedAt(Instant.now())
                .build();
    }

    @Test
    void createOrder_shouldCreateOrderSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(List.of(cartItem));
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(usdCurrency));
        when(productRepository.findById(1L)).thenReturn(Optional.of(activeProduct));
        when(variantRepository.getTotalStockByProductId(1L)).thenReturn(10);
        when(orderStatusRepository.findByCode("PENDING")).thenReturn(Optional.of(pendingStatus));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order saved = invocation.getArgument(0);
            saved.setUuid("order-uuid-1");
            saved.setOrderNumber("ORD-ABC123");
            saved.setCreatedAt(Instant.now());
            saved.setUpdatedAt(Instant.now());
            return saved;
        });

        OrderRequest request = new OrderRequest();
        request.setShippingAddressId(1L);
        request.setNotes("Leave at door");

        OrderResponse result = orderService.createOrder(1L, request);

        assertThat(result.getUuid()).isEqualTo("order-uuid-1");
        assertThat(result.getStatus().getCode()).isEqualTo("PENDING");
        verify(cartRepository).deleteAll(anyList());
        verify(eventPublisher).publishEvent(any(OrderCreatedEvent.class));
    }

    @Test
    void createOrder_withCoupon_shouldApplyDiscount() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(List.of(cartItem));
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(usdCurrency));
        when(productRepository.findById(1L)).thenReturn(Optional.of(activeProduct));
        when(variantRepository.getTotalStockByProductId(1L)).thenReturn(10);
        when(couponService.validateAndApply("SAVE10", 1L, BigDecimal.valueOf(100).setScale(4, java.math.RoundingMode.HALF_UP), null))
                .thenReturn(BigDecimal.TEN);
        when(orderStatusRepository.findByCode("PENDING")).thenReturn(Optional.of(pendingStatus));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order saved = invocation.getArgument(0);
            saved.setUuid("order-uuid-2");
            saved.setOrderNumber("ORD-DEF456");
            saved.setCreatedAt(Instant.now());
            saved.setUpdatedAt(Instant.now());
            return saved;
        });

        OrderRequest request = new OrderRequest();
        request.setShippingAddressId(1L);
        request.setCouponCode("SAVE10");

        OrderResponse result = orderService.createOrder(1L, request);

        assertThat(result.getUuid()).isEqualTo("order-uuid-2");
        verify(couponService).validateAndApply(eq("SAVE10"), eq(1L), any(), isNull());
    }

    @Test
    void createOrder_whenUserNotFound_shouldThrow() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        OrderRequest request = new OrderRequest();
        request.setShippingAddressId(1L);

        assertThatThrownBy(() -> orderService.createOrder(99L, request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void createOrder_whenCartEmpty_shouldThrow() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(List.of());

        OrderRequest request = new OrderRequest();
        request.setShippingAddressId(1L);

        assertThatThrownBy(() -> orderService.createOrder(1L, request))
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("Cart is empty");
    }

    @Test
    void createOrder_whenProductNotFound_shouldThrow() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(List.of(cartItem));
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(usdCurrency));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        OrderRequest request = new OrderRequest();
        request.setShippingAddressId(1L);

        assertThatThrownBy(() -> orderService.createOrder(1L, request))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void createOrder_whenProductInactive_shouldThrow() {
        CartItem inactiveCartItem = CartItem.builder().id(2L).user(user).product(inactiveProduct).quantity(1).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(List.of(inactiveCartItem));
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(usdCurrency));
        when(productRepository.findById(2L)).thenReturn(Optional.of(inactiveProduct));

        OrderRequest request = new OrderRequest();
        request.setShippingAddressId(1L);

        assertThatThrownBy(() -> orderService.createOrder(1L, request))
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("not active");
    }

    @Test
    void createOrder_whenInsufficientStock_shouldThrow() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(List.of(cartItem));
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(usdCurrency));
        when(productRepository.findById(1L)).thenReturn(Optional.of(activeProduct));
        when(variantRepository.getTotalStockByProductId(1L)).thenReturn(1);

        OrderRequest request = new OrderRequest();
        request.setShippingAddressId(1L);

        assertThatThrownBy(() -> orderService.createOrder(1L, request))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("Insufficient stock");
    }

    @Test
    void getOrderByUuid_shouldReturnOrder() {
        Order savedOrder = Order.builder()
                .id(1L).uuid("order-uuid-1").orderNumber("ORD-ABC123")
                .user(user).status(pendingStatus)
                .subtotal(BigDecimal.valueOf(100)).discount(BigDecimal.ZERO)
                .shippingCost(BigDecimal.ZERO).tax(BigDecimal.ZERO)
                .total(BigDecimal.valueOf(100))
                .currency(usdCurrency)
                .items(new ArrayList<>()).statusHistory(new ArrayList<>())
                .createdAt(Instant.now()).updatedAt(Instant.now())
                .build();

        when(orderRepository.findByUuid("order-uuid-1")).thenReturn(Optional.of(savedOrder));

        OrderResponse result = orderService.getOrderByUuid("order-uuid-1", 1L);

        assertThat(result.getUuid()).isEqualTo("order-uuid-1");
    }

    @Test
    void getOrderByUuid_whenNotOwner_shouldThrow() {
        when(orderRepository.findByUuid("order-uuid-1")).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.getOrderByUuid("order-uuid-1", 99L))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void getOrderByUuid_whenNotFound_shouldThrow() {
        when(orderRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderByUuid("nonexistent", 1L))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void getUserOrders_withPageable_shouldReturnPage() {
        PageRequest pageable = PageRequest.of(0, 20);
        Order savedOrder = Order.builder()
                .id(1L).uuid("order-uuid-1").orderNumber("ORD-ABC123")
                .user(user).status(pendingStatus)
                .subtotal(BigDecimal.valueOf(100)).discount(BigDecimal.ZERO)
                .shippingCost(BigDecimal.ZERO).tax(BigDecimal.ZERO)
                .total(BigDecimal.valueOf(100))
                .currency(usdCurrency)
                .items(new ArrayList<>()).statusHistory(new ArrayList<>())
                .createdAt(Instant.now()).updatedAt(Instant.now())
                .build();

        when(orderRepository.findByUserId(1L, pageable)).thenReturn(new PageImpl<>(List.of(savedOrder)));

        Page<OrderResponse> result = orderService.getUserOrders(1L, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUuid()).isEqualTo("order-uuid-1");
    }

    @Test
    void getUserOrders_withoutPageable_shouldReturnList() {
        Order savedOrder = Order.builder()
                .id(1L).uuid("order-uuid-1").orderNumber("ORD-ABC123")
                .user(user).status(pendingStatus)
                .subtotal(BigDecimal.valueOf(100)).discount(BigDecimal.ZERO)
                .shippingCost(BigDecimal.ZERO).tax(BigDecimal.ZERO)
                .total(BigDecimal.valueOf(100))
                .currency(usdCurrency)
                .items(new ArrayList<>()).statusHistory(new ArrayList<>())
                .createdAt(Instant.now()).updatedAt(Instant.now())
                .build();

        when(orderRepository.findByUserId(1L, Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(savedOrder)));

        List<OrderResponse> result = orderService.getUserOrders(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUuid()).isEqualTo("order-uuid-1");
    }

    @Test
    void updateOrderStatus_shouldUpdateSuccessfully() {
        when(orderRepository.findByUuid("order-uuid-1")).thenReturn(Optional.of(order));
        when(orderStatusRepository.findByCode("CONFIRMED")).thenReturn(Optional.of(confirmedStatus));
        when(orderStatusService.isValidTransition("PENDING", "CONFIRMED", "ADMIN")).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponse result = orderService.updateOrderStatus("order-uuid-1", "CONFIRMED", "Payment confirmed");

        assertThat(result).isNotNull();
        verify(eventPublisher).publishEvent(any(OrderStatusChangedEvent.class));
    }

    @Test
    void updateOrderStatus_whenNotFound_shouldThrow() {
        when(orderRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateOrderStatus("nonexistent", "CONFIRMED", "test"))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void updateOrderStatus_whenInvalidTransition_shouldThrow() {
        when(orderRepository.findByUuid("order-uuid-1")).thenReturn(Optional.of(order));
        when(orderStatusRepository.findByCode("CANCELLED")).thenReturn(Optional.of(cancelledStatus));
        when(orderStatusService.isValidTransition("PENDING", "CANCELLED", "ADMIN")).thenReturn(false);

        assertThatThrownBy(() -> orderService.updateOrderStatus("order-uuid-1", "CANCELLED", "not allowed"))
                .isInstanceOf(InvalidOrderStateException.class);
    }

    @Test
    void cancelOrder_shouldCancelSuccessfully() {
        when(orderRepository.findByUuid("order-uuid-1")).thenReturn(Optional.of(order));
        when(orderStatusRepository.findByCode("CANCELLED")).thenReturn(Optional.of(cancelledStatus));
        when(orderStatusService.isValidTransition("PENDING", "CANCELLED", "USER")).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponse result = orderService.cancelOrder("order-uuid-1", 1L);

        assertThat(result).isNotNull();
        verify(eventPublisher).publishEvent(any(OrderStatusChangedEvent.class));
    }

    @Test
    void cancelOrder_whenNotPending_shouldThrow() {
        Order shippedOrder = Order.builder()
                .id(2L).uuid("order-uuid-2").orderNumber("ORD-SHIP")
                .user(user).status(confirmedStatus)
                .subtotal(BigDecimal.valueOf(100)).discount(BigDecimal.ZERO)
                .shippingCost(BigDecimal.ZERO).tax(BigDecimal.ZERO)
                .total(BigDecimal.valueOf(100))
                .currency(usdCurrency)
                .items(new ArrayList<>()).statusHistory(new ArrayList<>())
                .createdAt(Instant.now()).updatedAt(Instant.now())
                .build();

        when(orderRepository.findByUuid("order-uuid-2")).thenReturn(Optional.of(shippedOrder));

        assertThatThrownBy(() -> orderService.cancelOrder("order-uuid-2", 1L))
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("Only PENDING orders can be cancelled");
    }

    @Test
    void cancelOrder_whenNotOwner_shouldThrow() {
        when(orderRepository.findByUuid("order-uuid-1")).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder("order-uuid-1", 99L))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void cancelOrder_whenNotFound_shouldThrow() {
        when(orderRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.cancelOrder("nonexistent", 1L))
                .isInstanceOf(OrderNotFoundException.class);
    }
}
