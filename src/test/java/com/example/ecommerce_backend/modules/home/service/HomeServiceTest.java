package com.example.ecommerce_backend.modules.home.service;

import com.example.ecommerce_backend.modules.brand.dto.response.BrandResponse;
import com.example.ecommerce_backend.modules.brand.service.BrandService;
import com.example.ecommerce_backend.modules.cart.dto.response.CartItemResponse;
import com.example.ecommerce_backend.modules.cart.service.CartService;
import com.example.ecommerce_backend.modules.category.dto.response.CategoryResponse;
import com.example.ecommerce_backend.modules.category.service.CategoryService;
import com.example.ecommerce_backend.modules.home.dto.DashboardResponse;
import com.example.ecommerce_backend.modules.home.dto.HomeResponse;
import com.example.ecommerce_backend.modules.order.dto.response.OrderResponse;
import com.example.ecommerce_backend.modules.order.service.OrderService;
import com.example.ecommerce_backend.modules.product.dto.response.ProductResponse;
import com.example.ecommerce_backend.modules.product.service.ProductService;
import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.wallet.dto.response.WalletResponse;
import com.example.ecommerce_backend.modules.wallet.service.WalletService;
import com.example.ecommerce_backend.modules.wishlist.dto.response.WishlistItemResponse;
import com.example.ecommerce_backend.modules.wishlist.service.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HomeServiceTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private BrandService brandService;

    @Mock
    private ProductService productService;

    @Mock
    private OrderService orderService;

    @Mock
    private CartService cartService;

    @Mock
    private WishlistService wishlistService;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private HomeService homeService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("user@test.com").build();
    }

    @Test
    void getHomeData_shouldReturnAllSections() {
        List<CategoryResponse> categories = List.of(
                CategoryResponse.builder().id(1L).name("Electronics").build()
        );
        List<BrandResponse> brands = List.of(
                BrandResponse.builder().id(1L).name("BrandA").build()
        );
        List<ProductResponse> newArrivals = List.of(
                ProductResponse.builder().uuid("uuid-1").name("New Arrival").build()
        );
        List<ProductResponse> featured = List.of(
                ProductResponse.builder().uuid("uuid-2").name("Featured").build()
        );

        when(categoryService.getTree(isNull())).thenReturn(categories);
        when(brandService.getAll(isNull())).thenReturn(brands);
        when(productService.getAllProducts(
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(),
                eq(true), isNull(), any(Sort.class)))
                .thenReturn(newArrivals);
        when(productService.getAllProducts(
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), eq(true),
                eq(true), isNull(), any(Sort.class)))
                .thenReturn(featured);

        HomeResponse result = homeService.getHomeData();

        assertThat(result.getCategories()).hasSize(1);
        assertThat(result.getBrands()).hasSize(1);
        assertThat(result.getNewArrivals()).hasSize(1);
        assertThat(result.getFeaturedProducts()).hasSize(1);
    }

    @Test
    void getHomeData_shouldLimitNewArrivalsToTen() {
        List<ProductResponse> manyProducts = List.of(
                ProductResponse.builder().uuid("uuid-1").build(),
                ProductResponse.builder().uuid("uuid-2").build(),
                ProductResponse.builder().uuid("uuid-3").build(),
                ProductResponse.builder().uuid("uuid-4").build(),
                ProductResponse.builder().uuid("uuid-5").build(),
                ProductResponse.builder().uuid("uuid-6").build(),
                ProductResponse.builder().uuid("uuid-7").build(),
                ProductResponse.builder().uuid("uuid-8").build(),
                ProductResponse.builder().uuid("uuid-9").build(),
                ProductResponse.builder().uuid("uuid-10").build(),
                ProductResponse.builder().uuid("uuid-11").build()
        );

        when(categoryService.getTree(isNull())).thenReturn(List.of());
        when(brandService.getAll(isNull())).thenReturn(List.of());
        when(productService.getAllProducts(
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(),
                eq(true), isNull(), any(Sort.class)))
                .thenReturn(manyProducts);
        when(productService.getAllProducts(
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), eq(true),
                eq(true), isNull(), any(Sort.class)))
                .thenReturn(List.of());

        HomeResponse result = homeService.getHomeData();

        assertThat(result.getNewArrivals()).hasSize(10);
    }

    @Test
    void getDashboard_shouldReturnDashboardWithNotificationServiceNull() {
        when(orderService.getUserOrders(1L)).thenReturn(List.of());
        when(wishlistService.getWishlist(user)).thenReturn(List.of());
        when(cartService.getCart(user)).thenReturn(List.of());
        WalletResponse walletResponse =
                WalletResponse.builder()
                        .balance(BigDecimal.valueOf(50.00)).build();
        when(walletService.getWallet(1L)).thenReturn(walletResponse);
        when(orderService.getUserOrders(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        DashboardResponse result = homeService.getDashboard(user);

        assertThat(result.getOrderCount()).isEqualTo(0);
        assertThat(result.getWishlistCount()).isEqualTo(0);
        assertThat(result.getCartCount()).isEqualTo(0);
        assertThat(result.getWalletBalance()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
        assertThat(result.getUnreadNotificationCount()).isEqualTo(0);
        assertThat(result.getRecentOrders()).isEmpty();
    }

    @Test
    void getDashboard_shouldCountOrdersAndItems() {
        OrderResponse order = OrderResponse.builder().uuid("order-uuid").build();
        WalletResponse walletResponse =
                WalletResponse.builder()
                        .balance(BigDecimal.valueOf(200.00)).build();

        when(orderService.getUserOrders(1L)).thenReturn(List.of(order, order));
        when(wishlistService.getWishlist(user)).thenReturn(List.of(
                WishlistItemResponse.builder().productUuid("item1").build(),
                WishlistItemResponse.builder().productUuid("item2").build(),
                WishlistItemResponse.builder().productUuid("item3").build()
        ));
        when(cartService.getCart(user)).thenReturn(List.of(
                CartItemResponse.builder().productUuid("cart1").build()
        ));
        when(walletService.getWallet(1L)).thenReturn(walletResponse);
        when(orderService.getUserOrders(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(order)));

        DashboardResponse result = homeService.getDashboard(user);

        assertThat(result.getOrderCount()).isEqualTo(2);
        assertThat(result.getWishlistCount()).isEqualTo(3);
        assertThat(result.getCartCount()).isEqualTo(1);
        assertThat(result.getRecentOrders()).hasSize(1);
    }
}
