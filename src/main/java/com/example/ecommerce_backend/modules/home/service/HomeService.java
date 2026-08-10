package com.example.ecommerce_backend.modules.home.service;

import com.example.ecommerce_backend.modules.brand.dto.response.BrandResponse;
import com.example.ecommerce_backend.modules.brand.service.BrandService;
import com.example.ecommerce_backend.modules.cart.service.CartService;
import com.example.ecommerce_backend.modules.category.dto.response.CategoryResponse;
import com.example.ecommerce_backend.modules.category.service.CategoryService;
import com.example.ecommerce_backend.modules.home.dto.DashboardAnalytics;
import com.example.ecommerce_backend.modules.home.dto.DashboardResponse;
import com.example.ecommerce_backend.modules.home.dto.HomeResponse;
import com.example.ecommerce_backend.modules.notification.service.NotificationService;
import com.example.ecommerce_backend.modules.order.dto.response.OrderResponse;
import com.example.ecommerce_backend.modules.order.entity.Order;
import com.example.ecommerce_backend.modules.order.entity.OrderStatus;
import com.example.ecommerce_backend.modules.order.mapper.OrderMapper;
import com.example.ecommerce_backend.modules.order.repository.OrderItemRepository;
import com.example.ecommerce_backend.modules.order.repository.OrderRepository;
import com.example.ecommerce_backend.modules.order.repository.OrderStatusRepository;
import com.example.ecommerce_backend.modules.order.service.OrderService;
import com.example.ecommerce_backend.modules.product.dto.response.ProductResponse;
import com.example.ecommerce_backend.modules.product.service.ProductService;
import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.user.repository.UserRepository;
import com.example.ecommerce_backend.modules.variant.entity.ProductVariant;
import com.example.ecommerce_backend.modules.variant.repository.ProductVariantRepository;
import com.example.ecommerce_backend.modules.wallet.service.WalletService;
import com.example.ecommerce_backend.modules.wishlist.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HomeService {

    private static final int TREND_DAYS = 7;
    private static final int LOW_STOCK_THRESHOLD = 5;
    private static final String SUPER_ADMIN = "SUPER_ADMIN";
    private static final String ADMIN = "ADMIN";

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private WalletService walletService;

    @Autowired(required = false)
    private NotificationService notificationService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    public HomeResponse getHomeData() {
        List<CategoryResponse> categories = categoryService.getTree(null);
        List<BrandResponse> brands = brandService.getAll(null);
        List<ProductResponse> newArrivals = productService.getAllProducts(
                null, null, null, null, null, null, null, true, null,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        List<ProductResponse> featured = productService.getAllProducts(
                null, null, null, null, null, null, true, true, null,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        int newArrivalsLimit = Math.min(newArrivals.size(), 10);
        int featuredLimit = Math.min(featured.size(), 10);

        return HomeResponse.builder()
                .categories(categories)
                .brands(brands)
                .newArrivals(newArrivals.subList(0, newArrivalsLimit))
                .featuredProducts(featured.subList(0, featuredLimit))
                .build();
    }

    public DashboardResponse getDashboard(User user) {
        boolean privileged = isPrivileged(user);
        long orderCount;
        long wishlistCount;
        long cartCount;
        BigDecimal walletBalance;
        long unreadCount;
        List<OrderResponse> recentOrders;

        if (privileged) {
            orderCount = orderRepository.count();
            wishlistCount = wishlistService.getWishlist(user).size();
            cartCount = cartService.getCart(user).size();
            walletBalance = walletService.getWallet(user.getId()).getBalance();
            unreadCount = notificationService != null ? notificationService.getUnreadCount(user.getId()) : 0;
            recentOrders = orderRepository
                    .findAllByOrderByCreatedAtDesc(PageRequest.of(0, 5))
                    .map(OrderMapper::toResponse)
                    .getContent();
        } else {
            orderCount = orderService.getUserOrders(user.getId()).size();
            wishlistCount = wishlistService.getWishlist(user).size();
            cartCount = cartService.getCart(user).size();
            walletBalance = walletService.getWallet(user.getId()).getBalance();
            unreadCount = notificationService != null ? notificationService.getUnreadCount(user.getId()) : 0;
            recentOrders = orderService.getUserOrders(
                    user.getId(),
                    PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))
            ).getContent();
        }

        return DashboardResponse.builder()
                .orderCount(orderCount)
                .wishlistCount(wishlistCount)
                .cartCount(cartCount)
                .walletBalance(walletBalance)
                .unreadNotificationCount(unreadCount)
                .recentOrders(recentOrders)
                .analytics(privileged ? buildAnalytics() : null)
                .build();
    }

    private boolean isPrivileged(User user) {
        if (user == null || user.getRole() == null) {
            return false;
        }
        String roleName = user.getRole().getRoleName();
        return SUPER_ADMIN.equals(roleName) || ADMIN.equals(roleName);
    }

    private DashboardAnalytics buildAnalytics() {
        BigDecimal totalRevenue = orderRepository.sumRevenue().setScale(2, RoundingMode.HALF_UP);
        long totalOrders = orderRepository.count();
        long totalCustomers = userRepository.count();

        List<Order> revenueSince = orderRepository.findRevenueSince(Instant.now().minusSeconds(TREND_DAYS * 86400L));
        List<DashboardAnalytics.RevenuePoint> revenueTrend = new ArrayList<>();
        for (int i = TREND_DAYS - 1; i >= 0; i--) {
            LocalDate day = LocalDate.now(ZoneOffset.UTC).minusDays(i);
            BigDecimal amount = revenueSince.stream()
                    .filter(o -> o.getCreatedAt() != null
                            && day.equals(o.getCreatedAt().atZone(ZoneOffset.UTC).toLocalDate()))
                    .map(Order::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);
            revenueTrend.add(DashboardAnalytics.RevenuePoint.builder().date(day).amount(amount).build());
        }

        Map<String, Long> countsByStatus = new HashMap<>();
        for (Object[] row : orderRepository.countByStatusName()) {
            countsByStatus.put((String) row[0], ((Number) row[1]).longValue());
        }
        List<DashboardAnalytics.StatusCount> statusBreakdown = new ArrayList<>();
        for (OrderStatus status : orderStatusRepository.findAllByOrderBySortOrderAsc()) {
            statusBreakdown.add(DashboardAnalytics.StatusCount.builder()
                    .status(status.getName())
                    .count(countsByStatus.getOrDefault(status.getName(), 0L))
                    .build());
        }

        List<DashboardAnalytics.TopProduct> topProducts = new ArrayList<>();
        for (Object[] row : orderItemRepository.findTopProducts(PageRequest.of(0, 5))) {
            topProducts.add(DashboardAnalytics.TopProduct.builder()
                    .productId((Long) row[0])
                    .productName((String) row[1])
                    .sku((String) row[2])
                    .quantitySold(((Number) row[3]).longValue())
                    .revenue(((BigDecimal) row[4]).setScale(2, RoundingMode.HALF_UP))
                    .build());
        }

        List<Instant> createdSince = userRepository.findCreatedSince(Instant.now().minusSeconds(TREND_DAYS * 86400L));
        List<DashboardAnalytics.GrowthPoint> userGrowth = new ArrayList<>();
        for (int i = TREND_DAYS - 1; i >= 0; i--) {
            LocalDate day = LocalDate.now(ZoneOffset.UTC).minusDays(i);
            long count = createdSince.stream()
                    .filter(c -> c != null && day.equals(c.atZone(ZoneOffset.UTC).toLocalDate()))
                    .count();
            userGrowth.add(DashboardAnalytics.GrowthPoint.builder().date(day).count(count).build());
        }

        List<ProductVariant> lowStock =
                productVariantRepository.findLowStock(LOW_STOCK_THRESHOLD, PageRequest.of(0, 10));
        List<DashboardAnalytics.LowStockItem> lowStockItems = lowStock.stream()
                .map(v -> DashboardAnalytics.LowStockItem.builder()
                        .productId(v.getProduct().getId())
                        .productName(v.getProduct().getName())
                        .sku(v.getSku())
                        .variantName(v.getName())
                        .stock(v.getStock())
                        .build())
                .toList();

        return DashboardAnalytics.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .totalCustomers(totalCustomers)
                .lowStockVariants(lowStock.size())
                .revenueTrend(revenueTrend)
                .orderStatusBreakdown(statusBreakdown)
                .topProducts(topProducts)
                .userGrowth(userGrowth)
                .lowStockItems(lowStockItems)
                .build();
    }
}