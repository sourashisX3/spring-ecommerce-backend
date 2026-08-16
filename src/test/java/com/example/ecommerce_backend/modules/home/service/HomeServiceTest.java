package com.example.ecommerce_backend.modules.home.service;

import com.example.ecommerce_backend.modules.banner.dto.response.BannerResponse;
import com.example.ecommerce_backend.modules.banner.service.BannerService;
import com.example.ecommerce_backend.modules.brand.dto.response.BrandResponse;
import com.example.ecommerce_backend.modules.brand.service.BrandService;
import com.example.ecommerce_backend.modules.cart.dto.response.CartItemResponse;
import com.example.ecommerce_backend.modules.cart.service.CartService;
import com.example.ecommerce_backend.modules.category.dto.response.CategoryResponse;
import com.example.ecommerce_backend.modules.category.service.CategoryService;
import com.example.ecommerce_backend.modules.home.dto.DashboardResponse;
import com.example.ecommerce_backend.modules.home.dto.HomeResponse;
import com.example.ecommerce_backend.modules.notification.service.NotificationService;
import com.example.ecommerce_backend.modules.offer.dto.response.OfferResponse;
import com.example.ecommerce_backend.modules.offer.service.OfferService;
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
import org.mockito.ArgumentCaptor;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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

    @Mock
    private BannerService bannerService;

    @Mock
    private OfferService offerService;

    @Mock
    private NotificationService notificationService;

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
                CategoryResponse.builder().id(1L).name("Whisky").build()
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
        List<ProductResponse> bestSellers = List.of(
                ProductResponse.builder().uuid("uuid-3").name("Best Seller").build()
        );
        List<ProductResponse> trending = List.of(
                ProductResponse.builder().uuid("uuid-4").name("Trending").build()
        );
        List<ProductResponse> deals = List.of(
                ProductResponse.builder().uuid("uuid-5").name("Deal").build()
        );
        List<BannerResponse> banners = List.of(
                BannerResponse.builder().uuid("banner-1").title("Summer Sale").build()
        );
        List<OfferResponse> offers = List.of(
                OfferResponse.builder().uuid("offer-1").title("Flash Offer").build()
        );

        when(categoryService.getTree(true)).thenReturn(categories);
        when(brandService.getAll(true)).thenReturn(brands);
        when(productService.getAllProducts(
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(),
                eq(true), isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(newArrivals));
        when(productService.getAllProducts(
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), eq(true),
                eq(true), isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(featured));
        when(productService.getAllProducts(
                isNull(), isNull(), eq(List.of("best-seller")), isNull(), isNull(), isNull(), isNull(),
                eq(true), isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(bestSellers));
        when(productService.getAllProducts(
                isNull(), isNull(), eq(List.of("trending")), isNull(), isNull(), isNull(), isNull(),
                eq(true), isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(trending));
        when(productService.getAllProducts(
                isNull(), isNull(), eq(List.of("sale")), isNull(), isNull(), isNull(), isNull(),
                eq(true), isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(deals));
        when(bannerService.getActiveBanners()).thenReturn(banners);
        when(offerService.getEligibleOffers(1L)).thenReturn(offers);
        when(walletService.getWallet(1L)).thenReturn(
                WalletResponse.builder().balance(BigDecimal.valueOf(150.59)).build());
        when(cartService.getCart(user)).thenReturn(List.of(
                CartItemResponse.builder().productUuid("cart1").build()));
        when(wishlistService.getWishlist(user)).thenReturn(List.of(
                WishlistItemResponse.builder().productUuid("wish1").build()));
        when(notificationService.getUnreadCount(1L)).thenReturn(3L);

        HomeResponse result = homeService.getHomeData(user);

        assertThat(result.getCategories()).hasSize(1);
        assertThat(result.getBrands()).hasSize(1);
        assertThat(result.getNewArrivals()).hasSize(1);
        assertThat(result.getFeaturedProducts()).hasSize(1);
        assertThat(result.getBestSellers()).hasSize(1);
        assertThat(result.getTrending()).hasSize(1);
        assertThat(result.getDeals()).hasSize(1);
        assertThat(result.getBanners()).hasSize(1);
        assertThat(result.getOffers()).hasSize(1);
        assertThat(result.getWalletBalance()).isEqualByComparingTo(BigDecimal.valueOf(150.59));
        assertThat(result.getCartCount()).isEqualTo(1);
        assertThat(result.getWishlistCount()).isEqualTo(1);
        assertThat(result.getUnreadNotificationCount()).isEqualTo(3);
        verify(productService).populateReviewStats(anyList());
        verify(categoryService).getTree(true);
        verify(brandService).getAll(true);
    }

    @Test
    void getHomeData_shouldUseRailLimitOfTen() {
        when(categoryService.getTree(true)).thenReturn(List.of());
        when(brandService.getAll(true)).thenReturn(List.of());
        when(productService.getAllProducts(
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(),
                eq(true), isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(
                        ProductResponse.builder().uuid("n1").build())));
        when(productService.getAllProducts(
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), eq(true),
                eq(true), isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
        when(productService.getAllProducts(
                isNull(), isNull(), anyList(), isNull(), isNull(), isNull(), isNull(),
                eq(true), isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
        when(bannerService.getActiveBanners()).thenReturn(List.of());
        when(walletService.getWallet(1L)).thenReturn(
                WalletResponse.builder().balance(BigDecimal.valueOf(10.00)).build());
        when(cartService.getCart(user)).thenReturn(List.of());
        when(wishlistService.getWishlist(user)).thenReturn(List.of());

        homeService.getHomeData(user);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(productService, atLeastOnce()).getAllProducts(
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(),
                eq(true), isNull(), captor.capture());
        assertThat(captor.getValue().getPageSize()).isEqualTo(10);
        assertThat(captor.getValue().getSort().isSorted()).isTrue();
    }

    @Test
    void getHomeData_whenUserNull_shouldReturnEmptyUserContext() {
        when(categoryService.getTree(true)).thenReturn(List.of());
        when(brandService.getAll(true)).thenReturn(List.of());
        when(productService.getAllProducts(
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(),
                eq(true), isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
        when(productService.getAllProducts(
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), eq(true),
                eq(true), isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
        when(productService.getAllProducts(
                isNull(), isNull(), anyList(), isNull(), isNull(), isNull(), isNull(),
                eq(true), isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
        when(bannerService.getActiveBanners()).thenReturn(List.of());
        when(offerService.getAll(eq(true), eq(true))).thenReturn(List.of());

        HomeResponse result = homeService.getHomeData(null);

        assertThat(result.getWalletBalance()).isNull();
        assertThat(result.getCartCount()).isZero();
        assertThat(result.getWishlistCount()).isZero();
        assertThat(result.getUnreadNotificationCount()).isZero();
        assertThat(result.getOffers()).isEmpty();
        verify(walletService, never()).getWallet(anyLong());
        verify(cartService, never()).getCart(any());
        verify(wishlistService, never()).getWishlist(any());
        verify(notificationService, never()).getUnreadCount(anyLong());
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