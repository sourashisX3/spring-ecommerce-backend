package com.example.ecommerce_backend.modules.home.service;

import com.example.ecommerce_backend.modules.brand.dto.response.BrandResponse;
import com.example.ecommerce_backend.modules.brand.service.BrandService;
import com.example.ecommerce_backend.modules.cart.service.CartService;
import com.example.ecommerce_backend.modules.category.dto.response.CategoryResponse;
import com.example.ecommerce_backend.modules.category.service.CategoryService;
import com.example.ecommerce_backend.modules.home.dto.DashboardResponse;
import com.example.ecommerce_backend.modules.home.dto.HomeResponse;
import com.example.ecommerce_backend.modules.notification.service.NotificationService;
import com.example.ecommerce_backend.modules.order.dto.response.OrderResponse;
import com.example.ecommerce_backend.modules.order.service.OrderService;
import com.example.ecommerce_backend.modules.product.dto.response.ProductResponse;
import com.example.ecommerce_backend.modules.product.service.ProductService;
import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.wallet.service.WalletService;
import com.example.ecommerce_backend.modules.wishlist.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class HomeService {

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
        long orderCount = orderService.getUserOrders(user.getId()).size();
        long wishlistCount = wishlistService.getWishlist(user).size();
        long cartCount = cartService.getCart(user).size();
        BigDecimal walletBalance = walletService.getWallet(user.getId()).getBalance();
        long unreadCount = notificationService != null ? notificationService.getUnreadCount(user.getId()) : 0;
        List<OrderResponse> recentOrders = orderService.getUserOrders(
                user.getId(),
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent();

        return DashboardResponse.builder()
                .orderCount(orderCount)
                .wishlistCount(wishlistCount)
                .cartCount(cartCount)
                .walletBalance(walletBalance)
                .unreadNotificationCount(unreadCount)
                .recentOrders(recentOrders)
                .build();
    }
}
