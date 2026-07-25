package com.example.ecommerce_backend.modules.order.service;

import com.example.ecommerce_backend.modules.cart.entity.CartItem;
import com.example.ecommerce_backend.modules.cart.repository.CartRepository;
import com.example.ecommerce_backend.modules.cart.service.CartService;
import com.example.ecommerce_backend.modules.coupon.service.CouponService;
import com.example.ecommerce_backend.modules.currency.entity.Currency;
import com.example.ecommerce_backend.modules.currency.exception.CurrencyNotFoundException;
import com.example.ecommerce_backend.modules.currency.repository.CurrencyRepository;
import com.example.ecommerce_backend.modules.order.dto.request.OrderRequest;
import com.example.ecommerce_backend.modules.order.dto.response.OrderResponse;
import com.example.ecommerce_backend.modules.order.entity.Order;
import com.example.ecommerce_backend.modules.order.entity.OrderItem;
import com.example.ecommerce_backend.modules.order.entity.OrderStatus;
import com.example.ecommerce_backend.modules.order.entity.OrderStatusHistory;
import com.example.ecommerce_backend.modules.order.exception.InsufficientStockException;
import com.example.ecommerce_backend.modules.order.exception.InvalidOrderStateException;
import com.example.ecommerce_backend.modules.order.exception.OrderNotFoundException;
import com.example.ecommerce_backend.modules.order.mapper.OrderMapper;
import com.example.ecommerce_backend.modules.order.repository.OrderRepository;
import com.example.ecommerce_backend.modules.order.repository.OrderStatusRepository;
import com.example.ecommerce_backend.modules.product.entity.Product;
import com.example.ecommerce_backend.modules.product.exception.ProductNotFoundException;
import com.example.ecommerce_backend.modules.product.repository.ProductRepository;
import com.example.ecommerce_backend.modules.variant.repository.ProductVariantRepository;
import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.user.exception.UserNotFoundException;
import com.example.ecommerce_backend.modules.user.repository.UserRepository;
import com.example.ecommerce_backend.core.event.OrderCreatedEvent;
import com.example.ecommerce_backend.core.event.OrderStatusChangedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private OrderStatusService orderStatusService;

    @Autowired
    private ProductVariantRepository variantRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional
    public OrderResponse createOrder(Long userId, OrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("id: " + userId));

        List<CartItem> cartItems = cartRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new InvalidOrderStateException("Cart is empty");
        }

        Currency currency = currencyRepository.findByCode("USD")
                .orElseThrow(() -> new CurrencyNotFoundException("USD"));

        Order order = Order.builder()
                .user(user)
                .subtotal(BigDecimal.ZERO)
                .discount(BigDecimal.ZERO)
                .shippingCost(BigDecimal.ZERO)
                .tax(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .currency(currency)
                .couponCode(request.getCouponCode())
                .notes(request.getNotes())
                .build();

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new ProductNotFoundException(
                            cartItem.getProduct().getUuid()));

            if (!product.isActive()) {
                throw new InvalidOrderStateException("Product is not active: " + product.getName());
            }

            int availableStock = variantRepository.getTotalStockByProductId(product.getId());
            if (cartItem.getQuantity() > availableStock) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName()
                        + ". Requested: " + cartItem.getQuantity() + ", Available: " + availableStock);
            }

            BigDecimal unitPrice = product.getBasePrice();

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(product.getId())
                    .productName(product.getName())
                    .sku(product.getSku())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(unitPrice)
                    .totalPrice(unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()))
                            .setScale(4, RoundingMode.HALF_UP))
                    .build();
            orderItems.add(orderItem);
        }

        order.setItems(orderItems);

        BigDecimal subtotal = orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(4, RoundingMode.HALF_UP);
        order.setSubtotal(subtotal);

        BigDecimal discount = BigDecimal.ZERO;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            discount = couponService.validateAndApply(request.getCouponCode(), userId, subtotal, null);
            order.setDiscount(discount);
        }

        BigDecimal total = subtotal.subtract(discount).add(order.getShippingCost()).add(order.getTax())
                .setScale(4, RoundingMode.HALF_UP);
        order.setTotal(total);

        OrderStatus pendingStatus = orderStatusRepository.findByCode("PENDING")
                .orElseThrow(() -> new InvalidOrderStateException("PENDING status not configured"));
        order.setStatus(pendingStatus);

        OrderStatusHistory statusHistory = OrderStatusHistory.builder()
                .order(order)
                .fromStatus(null)
                .toStatus(pendingStatus)
                .changedBy(user.getEmail())
                .build();
        order.setStatusHistory(List.of(statusHistory));

        order = orderRepository.save(order);

        cartRepository.deleteAll(cartItems);

        eventPublisher.publishEvent(new OrderCreatedEvent(this, userId, order.getUuid()));
        return OrderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderByUuid(String uuid, Long userId) {
        Order order = orderRepository.findByUuid(uuid)
                .orElseThrow(() -> new OrderNotFoundException(uuid));
        if (!order.getUser().getId().equals(userId)) {
            throw new OrderNotFoundException(uuid);
        }
        return OrderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(Long userId) {
        return getUserOrders(userId, Pageable.unpaged()).getContent();
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getUserOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(OrderMapper::toResponse);
    }

    @Transactional
    public OrderResponse updateOrderStatus(String uuid, String newStatusCode, String reason) {
        Order order = orderRepository.findByUuid(uuid)
                .orElseThrow(() -> new OrderNotFoundException(uuid));

        OrderStatus currentStatus = order.getStatus();
        OrderStatus newStatus = orderStatusRepository.findByCode(newStatusCode)
                .orElseThrow(() -> new InvalidOrderStateException("Status code not found: " + newStatusCode));

        if (!orderStatusService.isValidTransition(
                currentStatus != null ? currentStatus.getCode() : null,
                newStatus.getCode(), "ADMIN")) {
            throw new InvalidOrderStateException("Invalid status transition from " +
                    (currentStatus != null ? currentStatus.getCode() : "null") + " to " + newStatusCode);
        }

        order.setStatus(newStatus);

        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .fromStatus(currentStatus)
                .toStatus(newStatus)
                .changedBy("admin")
                .reason(reason)
                .build();

        order.getStatusHistory().add(history);
        order = orderRepository.save(order);
        eventPublisher.publishEvent(new OrderStatusChangedEvent(this, order.getUser().getId(), order.getUuid(), newStatusCode));
        return OrderMapper.toResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(String uuid, Long userId) {
        Order order = orderRepository.findByUuid(uuid)
                .orElseThrow(() -> new OrderNotFoundException(uuid));

        if (!order.getUser().getId().equals(userId)) {
            throw new OrderNotFoundException(uuid);
        }

        OrderStatus currentStatus = order.getStatus();
        if (currentStatus == null || !"PENDING".equals(currentStatus.getCode())) {
            throw new InvalidOrderStateException("Only PENDING orders can be cancelled");
        }

        OrderStatus cancelledStatus = orderStatusRepository.findByCode("CANCELLED")
                .orElseThrow(() -> new InvalidOrderStateException("CANCELLED status not configured"));

        if (!orderStatusService.isValidTransition("PENDING", "CANCELLED", "USER")) {
            throw new InvalidOrderStateException("Invalid status transition from PENDING to CANCELLED");
        }

        order.setStatus(cancelledStatus);
        order.setCanceledAt(Instant.now());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("id: " + userId));

        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .fromStatus(currentStatus)
                .toStatus(cancelledStatus)
                .changedBy(user.getEmail())
                .reason("Cancelled by user")
                .build();

        order.getStatusHistory().add(history);
        order = orderRepository.save(order);
        eventPublisher.publishEvent(new OrderStatusChangedEvent(this, userId, order.getUuid(), "CANCELLED"));
        return OrderMapper.toResponse(order);
    }
}
