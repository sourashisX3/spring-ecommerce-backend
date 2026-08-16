package com.example.ecommerce_backend.core.config;

import com.example.ecommerce_backend.modules.brand.entity.Brand;
import com.example.ecommerce_backend.modules.brand.repository.BrandRepository;
import com.example.ecommerce_backend.modules.banner.entity.Banner;
import com.example.ecommerce_backend.modules.banner.repository.BannerRepository;
import com.example.ecommerce_backend.modules.cart.entity.CartItem;
import com.example.ecommerce_backend.modules.cart.repository.CartRepository;
import com.example.ecommerce_backend.modules.category.entity.Category;
import com.example.ecommerce_backend.modules.category.repository.CategoryRepository;
import com.example.ecommerce_backend.modules.chat.entity.ChatMessage;
import com.example.ecommerce_backend.modules.chat.entity.ChatRoom;
import com.example.ecommerce_backend.modules.chat.repository.ChatMessageRepository;
import com.example.ecommerce_backend.modules.chat.repository.ChatRoomRepository;
import com.example.ecommerce_backend.modules.coupon.entity.Coupon;
import com.example.ecommerce_backend.modules.coupon.entity.CouponAssignment;
import com.example.ecommerce_backend.modules.coupon.repository.CouponAssignmentRepository;
import com.example.ecommerce_backend.modules.coupon.repository.CouponRepository;
import com.example.ecommerce_backend.modules.currency.entity.Currency;
import com.example.ecommerce_backend.modules.currency.repository.CurrencyRepository;
import com.example.ecommerce_backend.modules.discount.entity.Discount;
import com.example.ecommerce_backend.modules.discount.entity.DiscountAssignment;
import com.example.ecommerce_backend.modules.discount.repository.DiscountAssignmentRepository;
import com.example.ecommerce_backend.modules.discount.repository.DiscountRepository;
import com.example.ecommerce_backend.modules.discount.entity.DiscountType;
import com.example.ecommerce_backend.modules.discount.repository.DiscountTypeRepository;
import com.example.ecommerce_backend.modules.image.entity.ProductImage;
import com.example.ecommerce_backend.modules.image.repository.ProductImageRepository;
import com.example.ecommerce_backend.modules.notification.entity.Notification;
import com.example.ecommerce_backend.modules.notification.repository.NotificationRepository;
import com.example.ecommerce_backend.modules.offer.entity.Offer;
import com.example.ecommerce_backend.modules.offer.entity.OfferAssignment;
import com.example.ecommerce_backend.modules.offer.repository.OfferAssignmentRepository;
import com.example.ecommerce_backend.modules.offer.repository.OfferRepository;
import com.example.ecommerce_backend.modules.order.entity.OrderItem;
import com.example.ecommerce_backend.modules.order.entity.OrderStatus;
import com.example.ecommerce_backend.modules.order.entity.OrderStatusHistory;
import com.example.ecommerce_backend.modules.order.repository.OrderRepository;
import com.example.ecommerce_backend.modules.order.repository.OrderStatusRepository;
import com.example.ecommerce_backend.modules.payment.entity.Payment;
import com.example.ecommerce_backend.modules.payment.entity.PaymentGateway;
import com.example.ecommerce_backend.modules.payment.entity.PaymentStatus;
import com.example.ecommerce_backend.modules.payment.repository.PaymentGatewayRepository;
import com.example.ecommerce_backend.modules.payment.repository.PaymentRepository;
import com.example.ecommerce_backend.modules.payment.repository.PaymentStatusRepository;
import com.example.ecommerce_backend.modules.payment.repository.RefundRepository;
import com.example.ecommerce_backend.modules.payment.repository.RefundStatusRepository;
import com.example.ecommerce_backend.modules.payment.entity.Refund;
import com.example.ecommerce_backend.modules.payment.entity.RefundStatus;
import com.example.ecommerce_backend.modules.product.entity.Product;
import com.example.ecommerce_backend.modules.product.repository.ProductRepository;
import com.example.ecommerce_backend.modules.returns.entity.ReturnCondition;
import com.example.ecommerce_backend.modules.returns.entity.ReturnItem;
import com.example.ecommerce_backend.modules.returns.entity.ReturnRequest;
import com.example.ecommerce_backend.modules.returns.entity.ReturnStatus;
import com.example.ecommerce_backend.modules.returns.entity.ReturnType;
import com.example.ecommerce_backend.modules.returns.repository.ReturnConditionRepository;
import com.example.ecommerce_backend.modules.returns.repository.ReturnItemRepository;
import com.example.ecommerce_backend.modules.returns.repository.ReturnRequestRepository;
import com.example.ecommerce_backend.modules.returns.repository.ReturnStatusRepository;
import com.example.ecommerce_backend.modules.returns.repository.ReturnTypeRepository;
import com.example.ecommerce_backend.modules.review.entity.Review;
import com.example.ecommerce_backend.modules.review.repository.ReviewRepository;
import com.example.ecommerce_backend.modules.role.entity.Role;
import com.example.ecommerce_backend.modules.role.repository.RolesRepository;
import com.example.ecommerce_backend.modules.shipping.entity.ShippingAddress;
import com.example.ecommerce_backend.modules.shipping.repository.ShippingAddressRepository;
import com.example.ecommerce_backend.modules.tag.entity.Tag;
import com.example.ecommerce_backend.modules.tag.repository.TagRepository;
import com.example.ecommerce_backend.modules.user.entity.User;
import com.example.ecommerce_backend.modules.user.entity.UserAddress;
import com.example.ecommerce_backend.modules.user.repository.UserRepository;
import com.example.ecommerce_backend.modules.variant.entity.ProductVariant;
import com.example.ecommerce_backend.modules.variant.repository.ProductVariantRepository;
import com.example.ecommerce_backend.modules.wallet.entity.Wallet;
import com.example.ecommerce_backend.modules.wallet.entity.WalletTransaction;
import com.example.ecommerce_backend.modules.wallet.entity.WalletTransactionType;
import com.example.ecommerce_backend.modules.wallet.repository.WalletRepository;
import com.example.ecommerce_backend.modules.wallet.repository.WalletTransactionRepository;
import com.example.ecommerce_backend.modules.wallet.repository.WalletTransactionTypeRepository;
import com.example.ecommerce_backend.modules.wishlist.entity.WishlistItem;
import com.example.ecommerce_backend.modules.wishlist.repository.WishlistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import com.example.ecommerce_backend.modules.order.entity.Order;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Component
@Profile("dev")
@org.springframework.core.annotation.Order(Ordered.LOWEST_PRECEDENCE)
public class DummyDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DummyDataSeeder.class);
    private static final String PASSWORD = "Password@1";

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final WalletTransactionTypeRepository walletTransactionTypeRepository;
    private final CurrencyRepository currencyRepository;
    private final ShippingAddressRepository shippingAddressRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final TagRepository tagRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageRepository productImageRepository;
    private final DiscountTypeRepository discountTypeRepository;
    private final CouponRepository couponRepository;
    private final CouponAssignmentRepository couponAssignmentRepository;
    private final DiscountRepository discountRepository;
    private final DiscountAssignmentRepository discountAssignmentRepository;
    private final OfferRepository offerRepository;
    private final OfferAssignmentRepository offerAssignmentRepository;
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayRepository paymentGatewayRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final ReviewRepository reviewRepository;
    private final CartRepository cartRepository;
    private final WishlistRepository wishlistRepository;
    private final NotificationRepository notificationRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ReturnRequestRepository returnRequestRepository;
    private final ReturnItemRepository returnItemRepository;
    private final ReturnStatusRepository returnStatusRepository;
    private final ReturnTypeRepository returnTypeRepository;
    private final ReturnConditionRepository returnConditionRepository;
    private final RefundRepository refundRepository;
    private final RefundStatusRepository refundStatusRepository;
    private final BannerRepository bannerRepository;

    public DummyDataSeeder(PasswordEncoder passwordEncoder,
                           UserRepository userRepository,
                           RolesRepository rolesRepository,
                           WalletRepository walletRepository,
                           WalletTransactionRepository walletTransactionRepository,
                           WalletTransactionTypeRepository walletTransactionTypeRepository,
                           CurrencyRepository currencyRepository,
                           ShippingAddressRepository shippingAddressRepository,
                           CategoryRepository categoryRepository,
                           BrandRepository brandRepository,
                           TagRepository tagRepository,
                           ProductRepository productRepository,
                           ProductVariantRepository productVariantRepository,
                           ProductImageRepository productImageRepository,
                           DiscountTypeRepository discountTypeRepository,
                           CouponRepository couponRepository,
                           CouponAssignmentRepository couponAssignmentRepository,
                           DiscountRepository discountRepository,
                           DiscountAssignmentRepository discountAssignmentRepository,
                           OfferRepository offerRepository,
                           OfferAssignmentRepository offerAssignmentRepository,
                           OrderRepository orderRepository,
                           OrderStatusRepository orderStatusRepository,
                           PaymentRepository paymentRepository,
                           PaymentGatewayRepository paymentGatewayRepository,
                           PaymentStatusRepository paymentStatusRepository,
ReviewRepository reviewRepository,
                            CartRepository cartRepository,
                            WishlistRepository wishlistRepository,
                            NotificationRepository notificationRepository,
                            ChatRoomRepository chatRoomRepository,
                            ChatMessageRepository chatMessageRepository,
                            ReturnRequestRepository returnRequestRepository,
                            ReturnItemRepository returnItemRepository,
                            ReturnStatusRepository returnStatusRepository,
                            ReturnTypeRepository returnTypeRepository,
                            ReturnConditionRepository returnConditionRepository,
RefundRepository refundRepository,
                             RefundStatusRepository refundStatusRepository,
                             BannerRepository bannerRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
        this.walletRepository = walletRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.walletTransactionTypeRepository = walletTransactionTypeRepository;
        this.currencyRepository = currencyRepository;
        this.shippingAddressRepository = shippingAddressRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.tagRepository = tagRepository;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.productImageRepository = productImageRepository;
        this.discountTypeRepository = discountTypeRepository;
        this.couponRepository = couponRepository;
        this.couponAssignmentRepository = couponAssignmentRepository;
        this.discountRepository = discountRepository;
        this.discountAssignmentRepository = discountAssignmentRepository;
        this.offerRepository = offerRepository;
        this.offerAssignmentRepository = offerAssignmentRepository;
        this.orderRepository = orderRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.paymentRepository = paymentRepository;
        this.paymentGatewayRepository = paymentGatewayRepository;
        this.paymentStatusRepository = paymentStatusRepository;
        this.reviewRepository = reviewRepository;
        this.cartRepository = cartRepository;
        this.wishlistRepository = wishlistRepository;
        this.notificationRepository = notificationRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.returnRequestRepository = returnRequestRepository;
        this.returnItemRepository = returnItemRepository;
        this.returnStatusRepository = returnStatusRepository;
        this.returnTypeRepository = returnTypeRepository;
        this.returnConditionRepository = returnConditionRepository;
        this.refundRepository = refundRepository;
        this.refundStatusRepository = refundStatusRepository;
        this.bannerRepository = bannerRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        ensureSeedUserPasswords();
        refreshDemoRecency();
        migrateCurrencyReferences();

        if (userRepository.findByEmail("superadmin@example.com").isPresent()) {
            log.info("Dummy data already exists, refreshing catalog");
            seedCategories();
            seedBrands();
            deactivateLegacyCatalog();
            seedProducts();
            seedBulkCatalog();
            seedAssistantFlows();
            seedBanners();
            return;
        }

        seedUsers();
        seedShippingAddresses();
        seedWallets();
        seedWalletTransactions();
        seedCategories();
        seedBrands();
        seedTags();
        seedProducts();
        seedBulkCatalog();
        seedCoupons();
        seedDiscounts();
        seedOffers();
        seedOrders();
        seedPayments();
        seedReviews();
        seedCartItems();
        seedWishlistItems();
        seedNotifications();
        seedAssistantFlows();
        seedBanners();
    }

    private void seedBanners() {
        if (bannerRepository.count() > 0) {
            return;
        }
        Instant now = Instant.now();
        log.info("Seeding demo banners");
        bannerRepository.save(Banner.builder()
                .title("Summer Spirits Sale")
                .subtitle("Up to 40% off selected whisky, rum and gin")
                .imageUrl("https://placehold.co/1200x600/DB4460/FFFFFF?text=Summer+Spirits+Sale")
                .linkType("URL").linkValue("#deals")
                .sortOrder(1).isActive(true)
                .validFrom(now.minus(1, java.time.temporal.ChronoUnit.DAYS))
                .validUntil(now.plus(30, java.time.temporal.ChronoUnit.DAYS))
                .build());
        bannerRepository.save(Banner.builder()
                .title("New Season Arrivals")
                .subtitle("Fresh drops from the world's finest distilleries")
                .imageUrl("https://placehold.co/1200x600/3B3A5A/FFFFFF?text=New+Arrivals")
                .linkType("CATEGORY").linkValue("whisky")
                .sortOrder(2).isActive(true)
                .validFrom(now.minus(1, java.time.temporal.ChronoUnit.DAYS))
                .validUntil(now.plus(30, java.time.temporal.ChronoUnit.DAYS))
                .build());
        bannerRepository.save(Banner.builder()
                .title("Gift Packs & Sets")
                .subtitle("Curated collections for every occasion")
                .imageUrl("https://placehold.co/1200x600/1C1B1F/FFFFFF?text=Gift+Packs")
                .linkType("URL").linkValue("#gifts")
                .sortOrder(3).isActive(true)
                .validFrom(now.minus(1, java.time.temporal.ChronoUnit.DAYS))
                .validUntil(now.plus(30, java.time.temporal.ChronoUnit.DAYS))
                .build());
    }

    private void ensureSeedUserPasswords() {
        String encoded = passwordEncoder.encode(PASSWORD);
        for (String email : List.of("superadmin@example.com", "admin@example.com", "user@example.com")) {
            userRepository.findByEmail(email).ifPresent(user -> {
                log.info("Resetting password for seed user {}", email);
                user.setPassword(encoded);
                user.setActive(true);
                userRepository.save(user);
            });
        }
    }

    private void refreshDemoRecency() {
        Instant now = Instant.now();
        List<User> seedUsers = new ArrayList<>();
        for (String email : List.of("superadmin@example.com", "admin@example.com", "user@example.com")) {
            userRepository.findByEmail(email).ifPresent(seedUsers::add);
        }
        if (seedUsers.isEmpty()) {
            return;
        }

        List<Order> orders = new ArrayList<>();
        for (User seedUser : seedUsers) {
            orders.addAll(orderRepository.findByUserId(seedUser.getId()));
        }
        orders.sort(Comparator.comparing(Order::getId));
        for (int i = 0; i < orders.size(); i++) {
            int daysAgo = 6 - (i % 7);
            orders.get(i).setCreatedAt(now.minusSeconds(daysAgo * 86400L));
            orderRepository.save(orders.get(i));
        }

        for (int i = 0; i < seedUsers.size(); i++) {
            int daysAgo = Math.max(6 - i * 2, 1);
            User seedUser = seedUsers.get(i);
            seedUser.setCreatedAt(now.minusSeconds(daysAgo * 86400L));
            userRepository.save(seedUser);
        }
        log.info("Refreshed demo order/user timestamps to keep dashboard trends populated");
    }

    /**
     * Re-points records that reference the old INR default currency to USD.
     *
     * <p>Pricing in this app is USD-denominated, but older databases (and the
     * old default seed) recorded orders/payments/wallets with the INR currency
     * row. After {@link DataSeeder} promotes USD to the default, those records
     * would be misinterpreted as INR amounts and converted ~85x on display.
     * This migration rewrites them to USD so amounts render 1:1.
     */
    private void migrateCurrencyReferences() {
        Optional<Currency> usd = currencyRepository.findByCode("USD");
        Optional<Currency> inr = currencyRepository.findByCode("INR");
        if (usd.isEmpty() || inr.isEmpty()) {
            return;
        }
        Currency usdCurrency = usd.get();
        Currency inrCurrency = inr.get();
        int moved = 0;

        for (Order order : orderRepository.findAll()) {
            if (inrCurrency.getCode().equals(order.getCurrency().getCode())) {
                order.setCurrency(usdCurrency);
                orderRepository.save(order);
                moved++;
            }
        }
        for (Payment payment : paymentRepository.findAll()) {
            if (inrCurrency.getCode().equals(payment.getCurrency().getCode())) {
                payment.setCurrency(usdCurrency);
                paymentRepository.save(payment);
                moved++;
            }
        }
        for (Wallet wallet : walletRepository.findAll()) {
            if (inrCurrency.getCode().equals(wallet.getCurrency().getCode())) {
                wallet.setCurrency(usdCurrency);
                walletRepository.save(wallet);
                moved++;
            }
        }

        if (moved > 0) {
            log.info("Re-pointed {} INR-denominated orders/payments/wallets to USD", moved);
        }
    }

    private Role getRole(String name) {
        return rolesRepository.findByRoleName(name)
                .orElseThrow(() -> new RuntimeException("Role " + name + " not found"));
    }

    private Currency getCurrency(String code) {
        return currencyRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Currency " + code + " not found"));
    }

    private Currency getDefaultCurrency() {
        return currencyRepository.findByIsDefaultTrueAndIsActiveTrue()
                .orElseGet(() -> currencyRepository.findFirstByIsActiveTrueOrderBySortOrderAscIdAsc()
                        .orElseThrow(() -> new RuntimeException("No active default currency")));
    }

    private DiscountType getDiscountType(String code) {
        return discountTypeRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Discount type " + code + " not found"));
    }

    private OrderStatus getOrderStatus(String code) {
        return orderStatusRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Order status " + code + " not found"));
    }

    private PaymentGateway getPaymentGateway(String code) {
        return paymentGatewayRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Payment gateway " + code + " not found"));
    }

    private PaymentStatus getPaymentStatus(String code) {
        return paymentStatusRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Payment status " + code + " not found"));
    }

    private WalletTransactionType getWalletTransactionType(String code) {
        return walletTransactionTypeRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Wallet transaction type " + code + " not found"));
    }

    private void seedUsers() {
        Role superAdminRole = getRole("SUPER_ADMIN");
        Role adminRole = getRole("ADMIN");
        Role userRole = getRole("USER");
        String encoded = passwordEncoder.encode(PASSWORD);

        if (userRepository.findByEmail("superadmin@example.com").isEmpty()) {
            log.info("Seeding superadmin user");
            User user = User.builder()
                    .firstName("Super")
                    .lastName("Admin")
                    .email("superadmin@example.com")
                    .dialCode("+1")
                    .phoneNumber("+1-555-0100")
                    .password(encoded)
                    .role(superAdminRole)
                    .isActive(true)
                    .isEmailVerified(true)
                    .isPhoneVerified(true)
                    .address(makeAddress("100 Admin Blvd", "San Francisco", "CA", "US", 94105L))
                    .build();
            userRepository.save(user);
        }

        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            log.info("Seeding admin user");
            User user = User.builder()
                    .firstName("John")
                    .lastName("Admin")
                    .email("admin@example.com")
                    .dialCode("+1")
                    .phoneNumber("+1-555-0101")
                    .password(encoded)
                    .role(adminRole)
                    .isActive(true)
                    .isEmailVerified(true)
                    .isPhoneVerified(true)
                    .address(makeAddress("200 Admin Ave", "New York", "NY", "US", 10001L))
                    .build();
            userRepository.save(user);
        }

        if (userRepository.findByEmail("user@example.com").isEmpty()) {
            log.info("Seeding regular user");
            User user = User.builder()
                    .firstName("Jane")
                    .lastName("User")
                    .email("user@example.com")
                    .dialCode("+1")
                    .phoneNumber("+1-555-0102")
                    .password(encoded)
                    .role(userRole)
                    .isActive(true)
                    .isEmailVerified(true)
                    .isPhoneVerified(false)
                    .address(makeAddress("300 User St", "Austin", "TX", "US", 73301L))
                    .build();
            userRepository.save(user);
        }
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User " + email + " not found"));
    }

    private UserAddress makeAddress(String street, String city, String state, String country, Long zip) {
        UserAddress addr = new UserAddress();
        addr.setStreetAddress(street);
        addr.setCity(city);
        addr.setState(state);
        addr.setCountry(country);
        addr.setZipCode(zip);
        return addr;
    }

    private void seedShippingAddresses() {
        User user = getUser("user@example.com");
        User admin = getUser("admin@example.com");

        if (shippingAddressRepository.findByUserId(user.getId()).isEmpty()) {
            log.info("Seeding shipping addresses");

            shippingAddressRepository.save(ShippingAddress.builder()
                    .user(user)
                    .label("Home")
                    .recipientName("Jane User")
                    .phone("+1-555-0102")
                    .addressLine1("300 User St")
                    .city("Austin")
                    .state("TX")
                    .postalCode("73301")
                    .country("US")
                    .isDefault(true)
                    .build());

            shippingAddressRepository.save(ShippingAddress.builder()
                    .user(user)
                    .label("Work")
                    .recipientName("Jane User")
                    .phone("+1-555-0102")
                    .addressLine1("500 Business Park")
                    .city("Austin")
                    .state("TX")
                    .postalCode("73302")
                    .country("US")
                    .isDefault(false)
                    .build());

            shippingAddressRepository.save(ShippingAddress.builder()
                    .user(admin)
                    .label("Office")
                    .recipientName("John Admin")
                    .phone("+1-555-0101")
                    .addressLine1("200 Admin Ave")
                    .city("New York")
                    .state("NY")
                    .postalCode("10001")
                    .country("US")
                    .isDefault(true)
                    .build());
        }
    }

    private void seedWallets() {
        Currency currency = getDefaultCurrency();

        for (String email : List.of("superadmin@example.com", "admin@example.com", "user@example.com")) {
            User user = getUser(email);
            if (walletRepository.findByUserId(user.getId()).isEmpty()) {
                log.info("Seeding wallet for {}", email);
                walletRepository.save(Wallet.builder()
                        .user(user)
                        .balance(BigDecimal.valueOf(10000).setScale(4, RoundingMode.HALF_UP))
                        .currency(currency)
                        .isActive(true)
                        .build());
            }
        }
    }

    private void seedWalletTransactions() {
        WalletTransactionType credit = getWalletTransactionType("CREDIT");

        for (String email : List.of("superadmin@example.com", "admin@example.com", "user@example.com")) {
            User user = getUser(email);
            Wallet wallet = walletRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Wallet not found for " + email));

            if (walletTransactionRepository.findByWalletId(wallet.getId()).isEmpty()) {
                log.info("Seeding wallet transactions for {}", email);

                walletTransactionRepository.save(WalletTransaction.builder()
                        .wallet(wallet)
                        .type(credit)
                        .amount(BigDecimal.valueOf(10000).setScale(4, RoundingMode.HALF_UP))
                        .balanceBefore(BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP))
                        .balanceAfter(BigDecimal.valueOf(10000).setScale(4, RoundingMode.HALF_UP))
                        .referenceType("SIGNUP_BONUS")
                        .description("Welcome bonus for account registration")
                        .build());
            }
        }
    }

    private void seedCategories() {
        if (categoryRepository.findBySlug("whisky").isPresent()) {
            return;
        }
        log.info("Seeding liquor categories");

        Category spirits = categoryRepository.save(Category.builder()
                .name("Spirits")
                .slug("spirits")
                .description("Whisky, vodka, rum, gin, tequila and more")
                .imageUrl(IMG_HOST + "/400x400/3B3A5A/FFFFFF?text=Spirits")
                .sortOrder(1)
                .isActive(true)
                .build());

        saveCategory("Whisky", "whisky", "Scotch, bourbon, rye and single malts", "B87333", 1, spirits);
        saveCategory("Vodka", "vodka", "Smooth, versatile vodkas from around the world", "5B7B9A", 2, spirits);
        saveCategory("Rum", "rum", "White, golden, spiced and aged rums", "8B5A2B", 3, spirits);
        saveCategory("Gin", "gin", "London dry, old tom and contemporary gins", "3B7A57", 4, spirits);
        saveCategory("Tequila", "tequila", "Blanco, reposado and añejo tequilas", "C08A2E", 5, spirits);
        saveCategory("Brandy & Cognac", "brandy-cognac", "Fine brandies and cognacs", "A0522D", 6, spirits);
        saveCategory("Liqueurs", "liqueurs", "Sweet spirits and cream liqueurs", "6B3FA0", 7, spirits);
        saveCategory("Wine", "wine", "Red, white and rosé wines", "722F37", 8, null);
        saveCategory("Champagne", "champagne", "Sparkling wines and champagnes", "B08D57", 9, null);
    }

    private void saveCategory(String name, String slug, String description, String color, int sortOrder, Category parent) {
        categoryRepository.save(Category.builder()
                .name(name)
                .slug(slug)
                .description(description)
                .imageUrl(IMG_HOST + "/400x400/" + color + "/FFFFFF?text=" + name.replace(" ", "+"))
                .parent(parent)
                .sortOrder(sortOrder)
                .isActive(true)
                .build());
    }

    private Category getCategory(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Category " + slug + " not found"));
    }

    private void seedBrands() {
        Map<String, String[]> brands = new LinkedHashMap<>();
        brands.put("jack-daniels", new String[]{"Jack Daniel's", "Tennessee whiskey", "https://www.jackdaniels.com"});
        brands.put("johnnie-walker", new String[]{"Johnnie Walker", "Blended Scotch whisky", "https://www.johnniewalker.com"});
        brands.put("macallan", new String[]{"The Macallan", "Single malt Scotch whisky", "https://www.themacallan.com"});
        brands.put("glenfiddich", new String[]{"Glenfiddich", "Single malt Scotch whisky", "https://www.glenfiddich.com"});
        brands.put("jameson", new String[]{"Jameson", "Irish whiskey", "https://www.jamesonwhiskey.com"});
        brands.put("jim-beam", new String[]{"Jim Beam", "Kentucky straight bourbon", "https://www.jimbeam.com"});
        brands.put("absolut", new String[]{"Absolut", "Swedish vodka", "https://www.absolut.com"});
        brands.put("grey-goose", new String[]{"Grey Goose", "French vodka", "https://www.greygoose.com"});
        brands.put("belvedere", new String[]{"Belvedere", "Polish rye vodka", "https://www.belvederevodka.com"});
        brands.put("smirnoff", new String[]{"Smirnoff", "Premium vodka", "https://www.smirnoff.com"});
        brands.put("bacardi", new String[]{"Bacardi", "White and aged rums", "https://www.bacardi.com"});
        brands.put("captain-morgan", new String[]{"Captain Morgan", "Spiced rum", "https://www.captainmorgan.com"});
        brands.put("hendricks", new String[]{"Hendrick's", "Small-batch gin", "https://www.hendricksgin.com"});
        brands.put("bombay-sapphire", new String[]{"Bombay Sapphire", "London dry gin", "https://www.bombaysapphire.com"});
        brands.put("tanqueray", new String[]{"Tanqueray", "London dry gin", "https://www.tanqueray.com"});
        brands.put("don-julio", new String[]{"Don Julio", "Premium tequila", "https://www.donjulio.com"});
        brands.put("jose-cuervo", new String[]{"José Cuervo", "Tequila", "https://www.cuervo.com"});
        brands.put("patron", new String[]{"Patrón", "Ultra-premium tequila", "https://www.patrontequila.com"});
        brands.put("remy-martin", new String[]{"Rémy Martin", "Fine cognac", "https://www.remymartin.com"});
        brands.put("baileys", new String[]{"Baileys", "Irish cream liqueur", "https://www.baileys.com"});
        brands.put("moet-chandon", new String[]{"Moët & Chandon", "Champagne", "https://www.moet.com"});
        brands.put("veuve-clicquot", new String[]{"Veuve Clicquot", "Champagne", "https://www.veuveclicquot.com"});
        brands.put("jacobs-creek", new String[]{"Jacob's Creek", "Australian wine", "https://www.jacobscreek.com"});
        brands.put("yellow-tail", new String[]{"Yellow Tail", "Australian wine", "https://www.yellowtailwine.com"});

        for (Map.Entry<String, String[]> entry : brands.entrySet()) {
            if (brandRepository.findBySlug(entry.getKey()).isEmpty()) {
                String[] val = entry.getValue();
                log.info("Seeding brand: {}", val[0]);
                brandRepository.save(Brand.builder()
                        .name(val[0])
                        .slug(entry.getKey())
                        .description(val[1])
                        .website(val[2])
                        .isActive(true)
                        .build());
            }
        }
    }

    private Brand getBrand(String slug) {
        return brandRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Brand " + slug + " not found"));
    }

    private void seedTags() {
        Map<String, String> tags = new LinkedHashMap<>();
        tags.put("new-arrival", "New Arrival");
        tags.put("best-seller", "Best Seller");
        tags.put("sale", "Sale");
        tags.put("featured", "Featured");
        tags.put("trending", "Trending");
        tags.put("limited-edition", "Limited Edition");

        for (Map.Entry<String, String> entry : tags.entrySet()) {
            if (tagRepository.findBySlug(entry.getKey()).isEmpty()) {
                log.info("Seeding tag: {}", entry.getValue());
                tagRepository.save(Tag.builder()
                        .name(entry.getValue())
                        .slug(entry.getKey())
                        .isActive(true)
                        .build());
            }
        }
    }

    private Tag getTag(String slug) {
        return tagRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Tag " + slug + " not found"));
    }

    private static final String IMG_HOST = "https://placehold.co";

    private static final Map<String, String> CATEGORY_COLORS = Map.of(
            "whisky", "B87333",
            "vodka", "5B7B9A",
            "rum", "8B5A2B",
            "gin", "3B7A57",
            "tequila", "C08A2E",
            "brandy-cognac", "A0522D",
            "liqueurs", "6B3FA0",
            "wine", "722F37",
            "champagne", "B08D57");

    private static String img(String text, String color) {
        return IMG_HOST + "/800x800/" + color + "/FFFFFF?text=" + text.replace(" ", "+");
    }

    private void deactivateLegacyCatalog() {
        List<String> legacyCategorySlugs = List.of(
                "electronics", "clothing", "home-kitchen", "mobile-phones",
                "laptops", "headphones", "mens-clothing", "womens-clothing");
        List<String> legacyBrandSlugs = List.of(
                "apple", "samsung", "sony", "nike", "adidas", "dell", "lg");

        for (String slug : legacyCategorySlugs) {
            categoryRepository.findBySlug(slug).ifPresent(c -> {
                c.setActive(false);
                categoryRepository.save(c);
            });
        }
        for (String slug : legacyBrandSlugs) {
            brandRepository.findBySlug(slug).ifPresent(b -> {
                b.setActive(false);
                brandRepository.save(b);
            });
        }

        int deactivated = 0;
        for (Product product : productRepository.findAll()) {
            if (product.getCategory() != null && legacyCategorySlugs.contains(product.getCategory().getSlug())) {
                product.setActive(false);
                productRepository.save(product);
                deactivated++;
            }
        }
        if (deactivated > 0) {
            log.info("Deactivated {} legacy non-liquor products", deactivated);
        }
    }

    private void seedBulkCatalog() {
        if (productRepository.findBySlug("johnnie-walker-black-label").isPresent()) {
            return;
        }
        log.info("Seeding bulk catalog ({} products)", BULK_PRODUCTS.size());

        Tag bestSeller = getTag("best-seller");
        Tag sale = getTag("sale");
        Tag featured = getTag("featured");
        Tag trending = getTag("trending");

        for (int i = 0; i < BULK_PRODUCTS.size(); i++) {
            BulkProduct b = BULK_PRODUCTS.get(i);
            String color = CATEGORY_COLORS.getOrDefault(b.category(), "3B3A5A");
            Product product = productRepository.save(Product.builder()
                    .sku(b.sku())
                    .name(b.name())
                    .slug(b.slug())
                    .description(b.name() + " - a premium pour for every occasion, selected by our spirits experts.")
                    .shortDescription("Popular " + b.name() + " selection")
                    .basePrice(BigDecimal.valueOf(b.price()))
                    .category(getCategory(b.category()))
                    .brand(getBrand(b.brand()))
                    .tags(i % 3 == 0 ? Set.of(bestSeller, trending) : i % 4 == 0 ? Set.of(featured, sale) : Set.of(sale))
                    .isActive(b.active())
                    .isFeatured(i % 5 == 0)
                    .attributes(Map.of("size", b.size(), "distillery", b.brand()))
                    .build());

            productVariantRepository.save(ProductVariant.builder()
                    .sku(b.sku() + "-STD")
                    .name(b.name() + " (750ml)")
                    .price(BigDecimal.valueOf(b.price()))
                    .stock(b.stock())
                    .product(product)
                    .isActive(b.active())
                    .isDefault(true)
                    .sortOrder(0)
                    .attributes(Map.of("configuration", "750ml"))
                    .build());

            seedProductImages(product, b.name(), List.of(
                    img(b.name(), color),
                    img(b.name() + " Bottle", color)));
        }
    }

    private record BulkProduct(String slug, String name, String sku, double price,
                               String category, String brand, String size, int stock, boolean active) {
    }

    private static final List<BulkProduct> BULK_PRODUCTS = List.of(
            new BulkProduct("johnnie-walker-black-label", "Johnnie Walker Black Label", "JW-BL-001", 34.99, "whisky", "johnnie-walker", "750ml", 42, true),
            new BulkProduct("johnnie-walker-blue-label", "Johnnie Walker Blue Label", "JW-BLU-001", 189.99, "whisky", "johnnie-walker", "750ml", 18, true),
            new BulkProduct("macallan-18-year", "The Macallan 18 Year Old", "MC-18-001", 329.99, "whisky", "macallan", "750ml", 8, true),
            new BulkProduct("glenfiddich-12-year", "Glenfiddich 12 Year Old", "GF-12-001", 49.99, "whisky", "glenfiddich", "750ml", 30, true),
            new BulkProduct("jameson-irish-whiskey", "Jameson Irish Whiskey", "JM-001-001", 28.99, "whisky", "jameson", "750ml", 46, true),
            new BulkProduct("jim-beam-bourbon", "Jim Beam Kentucky Straight Bourbon", "JB-001-001", 21.99, "whisky", "jim-beam", "750ml", 52, true),
            new BulkProduct("jack-daniels-tennessee-honey", "Jack Daniel's Tennessee Honey", "JD-TH-001", 27.99, "whisky", "jack-daniels", "750ml", 38, true),
            new BulkProduct("absolut-citron", "Absolut Citron", "ABS-CIT-001", 22.99, "vodka", "absolut", "750ml", 44, true),
            new BulkProduct("grey-goose-vodka", "Grey Goose Vodka", "GG-001-001", 39.99, "vodka", "grey-goose", "750ml", 26, true),
            new BulkProduct("belvedere-vodka", "Belvedere Vodka", "BEL-001-001", 44.99, "vodka", "belvedere", "750ml", 22, true),
            new BulkProduct("smirnoff-no-21", "Smirnoff No. 21 Vodka", "SM-21-001", 19.99, "vodka", "smirnoff", "750ml", 55, true),
            new BulkProduct("bacardi-gold", "Bacardi Gold", "BAC-GOLD-001", 21.99, "rum", "bacardi", "750ml", 48, true),
            new BulkProduct("captain-morgan-spiced", "Captain Morgan Spiced Rum", "CM-SP-001", 22.99, "rum", "captain-morgan", "750ml", 45, true),
            new BulkProduct("captain-morgan-private-stock", "Captain Morgan Private Stock", "CM-PS-001", 27.99, "rum", "captain-morgan", "750ml", 28, true),
            new BulkProduct("hendricks-orbium", "Hendrick's Orbium", "HEN-ORB-001", 42.99, "gin", "hendricks", "750ml", 20, true),
            new BulkProduct("bombay-sapphire-gin", "Bombay Sapphire Gin", "BOM-SAP-001", 29.99, "gin", "bombay-sapphire", "750ml", 36, true),
            new BulkProduct("tanqueray-london-dry", "Tanqueray London Dry Gin", "TAN-LD-001", 28.99, "gin", "tanqueray", "750ml", 39, true),
            new BulkProduct("don-julio-blanco", "Don Julio Blanco", "DJ-BL-001", 49.99, "tequila", "don-julio", "750ml", 24, true),
            new BulkProduct("don-julio-anejo", "Don Julio Añejo", "DJ-AN-001", 89.99, "tequila", "don-julio", "750ml", 15, true),
            new BulkProduct("jose-cuervo-especial", "José Cuervo Especial Gold", "JC-ES-001", 24.99, "tequila", "jose-cuervo", "750ml", 41, true),
            new BulkProduct("patron-silver", "Patrón Silver", "PAT-SIL-001", 49.99, "tequila", "patron", "750ml", 23, true),
            new BulkProduct("remy-martin-vsop", "Rémy Martin VSOP", "RM-VSOP-001", 54.99, "brandy-cognac", "remy-martin", "750ml", 17, true),
            new BulkProduct("baileys-irish-cream", "Baileys Irish Cream", "BAI-001-001", 27.99, "liqueurs", "baileys", "750ml", 40, true),
            new BulkProduct("jacobs-creek-shiraz", "Jacob's Creek Shiraz", "JCW-SH-001", 12.99, "wine", "jacobs-creek", "750ml", 60, true),
            new BulkProduct("yellow-tail-cabernet", "Yellow Tail Cabernet Sauvignon", "YT-CAB-001", 11.99, "wine", "yellow-tail", "750ml", 62, true),
            new BulkProduct("veuve-clicquot-yellow-label", "Veuve Clicquot Yellow Label", "VC-YL-001", 69.99, "champagne", "veuve-clicquot", "750ml", 16, true));

    private void seedProducts() {
        Category whisky = getCategory("whisky");
        Category vodka = getCategory("vodka");
        Category rum = getCategory("rum");
        Category gin = getCategory("gin");
        Category champagne = getCategory("champagne");

        Brand jackDaniels = getBrand("jack-daniels");
        Brand macallan = getBrand("macallan");
        Brand absolut = getBrand("absolut");
        Brand bacardi = getBrand("bacardi");
        Brand hendricks = getBrand("hendricks");
        Brand moetChandon = getBrand("moet-chandon");

        Tag newArrival = getTag("new-arrival");
        Tag bestSeller = getTag("best-seller");
        Tag sale = getTag("sale");
        Tag featured = getTag("featured");
        Tag trending = getTag("trending");
        Tag limitedEdition = getTag("limited-edition");

        if (productRepository.findBySlug("jack-daniels-old-no-7").isEmpty()) {
            log.info("Seeding flagship products");

            Product jackDanielsOldNo7 = productRepository.save(Product.builder()
                    .sku("JD-ON7-001")
                    .name("Jack Daniel's Old No. 7")
                    .slug("jack-daniels-old-no-7")
                    .description("America's favorite Tennessee whiskey. Charcoal-mellowed drop by drop, with notes of caramel, vanilla and toasted oak.")
                    .shortDescription("Iconic Tennessee whiskey, charcoal-mellowed")
                    .basePrice(BigDecimal.valueOf(32.99))
                    .category(whisky)
                    .brand(jackDaniels)
                    .tags(Set.of(bestSeller, featured, trending))
                    .isActive(true)
                    .isFeatured(true)
                    .attributes(Map.of("size", "750ml", "type", "Tennessee Whiskey"))
                    .build());

            Product macallan12 = productRepository.save(Product.builder()
                    .sku("MC-12-001")
                    .name("The Macallan 12 Year Old")
                    .slug("macallan-12-year")
                    .description("A single malt aged in sherry-seasoned oak casks from Jerez, delivering rich dried fruit, wood spice and warm oak.")
                    .shortDescription("Sherry oak single malt Scotch whisky")
                    .basePrice(BigDecimal.valueOf(89.99))
                    .category(whisky)
                    .brand(macallan)
                    .tags(Set.of(newArrival, featured, limitedEdition))
                    .isActive(true)
                    .isFeatured(true)
                    .attributes(Map.of("size", "750ml", "type", "Single Malt Scotch"))
                    .build());

            Product absolutVodka = productRepository.save(Product.builder()
                    .sku("ABS-001-001")
                    .name("Absolut Vodka")
                    .slug("absolut-vodka")
                    .description("Crafted from Swedish winter wheat and water from the village of Åhus. Clean, smooth and distinctively rich.")
                    .shortDescription("Smooth Swedish vodka made from winter wheat")
                    .basePrice(BigDecimal.valueOf(24.99))
                    .category(vodka)
                    .brand(absolut)
                    .tags(Set.of(bestSeller, sale))
                    .isActive(true)
                    .isFeatured(true)
                    .attributes(Map.of("size", "750ml", "type", "Vodka"))
                    .build());

            Product bacardiSuperior = productRepository.save(Product.builder()
                    .sku("BAC-SUP-001")
                    .name("Bacardi Superior")
                    .slug("bacardi-superior")
                    .description("Light, clean-tasting white rum aged in charred American oak barrels and filtered through charcoal.")
                    .shortDescription("The world's most awarded white rum")
                    .basePrice(BigDecimal.valueOf(19.99))
                    .category(rum)
                    .brand(bacardi)
                    .tags(Set.of(sale, trending))
                    .isActive(true)
                    .isFeatured(false)
                    .attributes(Map.of("size", "750ml", "type", "White Rum"))
                    .build());

            Product hendricksGin = productRepository.save(Product.builder()
                    .sku("HEN-001-001")
                    .name("Hendrick's Gin")
                    .slug("hendricks-gin")
                    .description("Small-batch gin infused with cucumber and rose petals, distilled in tiny copper stills for an unusual, smooth finish.")
                    .shortDescription("Unusual gin infused with cucumber and rose")
                    .basePrice(BigDecimal.valueOf(39.99))
                    .category(gin)
                    .brand(hendricks)
                    .tags(Set.of(newArrival, bestSeller, featured))
                    .isActive(true)
                    .isFeatured(true)
                    .attributes(Map.of("size", "750ml", "type", "Gin"))
                    .build());

            Product moetImperial = productRepository.save(Product.builder()
                    .sku("MC-IMP-001")
                    .name("Moët & Chandon Impérial")
                    .slug("moet-chandon-imperial")
                    .description("The iconic house style of Moët & Chandon - a bright, generous and expressive brut champagne with green apple and citrus.")
                    .shortDescription("The signature brut champagne from Moët & Chandon")
                    .basePrice(BigDecimal.valueOf(59.99))
                    .category(champagne)
                    .brand(moetChandon)
                    .tags(Set.of(featured, limitedEdition))
                    .isActive(true)
                    .isFeatured(true)
                    .attributes(Map.of("size", "750ml", "type", "Brut Champagne"))
                    .build());

            seedVariantsAndImages(jackDanielsOldNo7, macallan12, absolutVodka, bacardiSuperior, hendricksGin, moetImperial);
        }
    }

    private void seedVariantsAndImages(Product jackDaniels, Product macallan12, Product absolutVodka,
                                       Product bacardiSuperior, Product hendricksGin, Product moetImperial) {
        log.info("Seeding variants and images");

        seedLiquorVariants(jackDaniels, "JD-ON7", "Jack Daniel's Old No. 7",
                List.of("200ml", "375ml", "750ml", "1L"),
                List.of(BigDecimal.valueOf(-20), BigDecimal.valueOf(-10), BigDecimal.ZERO, BigDecimal.valueOf(8)),
                List.of(30, 40, 60, 35));
        seedLiquorVariants(macallan12, "MC-12", "The Macallan 12 Year Old",
                List.of("375ml", "750ml", "1L"),
                List.of(BigDecimal.valueOf(-25), BigDecimal.ZERO, BigDecimal.valueOf(45)),
                List.of(20, 30, 12));
        seedLiquorVariants(absolutVodka, "ABS", "Absolut Vodka",
                List.of("200ml", "375ml", "750ml", "1L"),
                List.of(BigDecimal.valueOf(-15), BigDecimal.valueOf(-7), BigDecimal.ZERO, BigDecimal.valueOf(6)),
                List.of(35, 45, 55, 40));
        seedLiquorVariants(bacardiSuperior, "BAC-SUP", "Bacardi Superior",
                List.of("200ml", "375ml", "750ml", "1L"),
                List.of(BigDecimal.valueOf(-12), BigDecimal.valueOf(-6), BigDecimal.ZERO, BigDecimal.valueOf(5)),
                List.of(40, 50, 60, 45));
        seedLiquorVariants(hendricksGin, "HEN", "Hendrick's Gin",
                List.of("375ml", "750ml", "1L"),
                List.of(BigDecimal.valueOf(-12), BigDecimal.ZERO, BigDecimal.valueOf(22)),
                List.of(25, 35, 15));
        seedLiquorVariants(moetImperial, "MC-IMP", "Moët & Chandon Impérial",
                List.of("375ml", "750ml", "1500ml"),
                List.of(BigDecimal.valueOf(-20), BigDecimal.ZERO, BigDecimal.valueOf(60)),
                List.of(15, 25, 8));

        seedProductImages(jackDaniels, "Jack Daniel's Old No. 7", List.of(
                img("Jack Daniel's Old No. 7", "B87333"),
                img("Jack Daniel's Old No. 7 Bottle", "B87333")));
        seedProductImages(macallan12, "The Macallan 12 Year Old", List.of(
                img("The Macallan 12 Year Old", "A0522D"),
                img("The Macallan 12 Year Old Bottle", "A0522D")));
        seedProductImages(absolutVodka, "Absolut Vodka", List.of(
                img("Absolut Vodka", "5B7B9A"),
                img("Absolut Vodka Bottle", "5B7B9A")));
        seedProductImages(bacardiSuperior, "Bacardi Superior", List.of(
                img("Bacardi Superior", "8B5A2B"),
                img("Bacardi Superior Bottle", "8B5A2B")));
        seedProductImages(hendricksGin, "Hendrick's Gin", List.of(
                img("Hendrick's Gin", "3B7A57"),
                img("Hendrick's Gin Bottle", "3B7A57")));
        seedProductImages(moetImperial, "Moët & Chandon Impérial", List.of(
                img("Moët & Chandon Impérial", "B08D57"),
                img("Moët & Chandon Impérial Bottle", "B08D57")));
    }

    private void seedLiquorVariants(Product product, String skuPrefix, String baseName,
                                    List<String> sizes, List<BigDecimal> priceAdjustments, List<Integer> stocks) {
        for (int i = 0; i < sizes.size(); i++) {
            boolean isDefault = i == 0;
            BigDecimal price = product.getBasePrice().add(priceAdjustments.get(i));
            String sizeCode = sizes.get(i).replace("ml", "ML");
            productVariantRepository.save(ProductVariant.builder()
                    .sku(skuPrefix + "-" + sizeCode)
                    .name(baseName + " (" + sizes.get(i) + ")")
                    .price(price)
                    .stock(stocks.get(i))
                    .product(product)
                    .isActive(true)
                    .isDefault(isDefault)
                    .sortOrder(i)
                    .attributes(Map.of("size", sizes.get(i)))
                    .build());
        }
    }

    private void seedProductImages(Product product, String altText, List<String> urls) {
        for (int i = 0; i < urls.size(); i++) {
            productImageRepository.save(ProductImage.builder()
                    .imageUrl(urls.get(i))
                    .isPrimary(i == 0)
                    .sortOrder(i)
                    .product(product)
                    .build());
        }
    }

    private void seedCoupons() {
        DiscountType percentage = getDiscountType("PERCENTAGE");
        DiscountType fixedAmount = getDiscountType("FIXED_AMOUNT");
        Instant now = Instant.now();
        Instant future = now.plus(Duration.ofDays(90));

        if (couponRepository.findByCode("WELCOME10").isEmpty()) {
            log.info("Seeding coupons");

            couponRepository.save(Coupon.builder()
                    .code("WELCOME10")
                    .description("10% off your first purchase")
                    .discountType(percentage)
                    .discountValue(BigDecimal.valueOf(10))
                    .maxDiscount(BigDecimal.valueOf(50))
                    .minOrderAmount(BigDecimal.valueOf(50))
                    .usageLimit(1000)
                    .usageLimitPerUser(1)
                    .isGlobal(true)
                    .isActive(true)
                    .validFrom(now)
                    .validUntil(future)
                    .build());

            couponRepository.save(Coupon.builder()
                    .code("SAVE25")
                    .description("$25 off orders over $100")
                    .discountType(fixedAmount)
                    .discountValue(BigDecimal.valueOf(25))
                    .minOrderAmount(BigDecimal.valueOf(100))
                    .usageLimit(500)
                    .usageLimitPerUser(2)
                    .isGlobal(true)
                    .isActive(true)
                    .validFrom(now)
                    .validUntil(future)
                    .build());

            couponRepository.save(Coupon.builder()
                    .code("VIP50")
                    .description("$50 off premium whiskies")
                    .discountType(fixedAmount)
                    .discountValue(BigDecimal.valueOf(50))
                    .minOrderAmount(BigDecimal.valueOf(500))
                    .maxDiscount(BigDecimal.valueOf(50))
                    .usageLimit(100)
                    .usageLimitPerUser(1)
                    .isGlobal(false)
                    .isActive(true)
                    .validFrom(now)
                    .validUntil(future)
                    .build());
        }

        Coupon welcome10 = couponRepository.findByCode("WELCOME10").get();
        Coupon save25 = couponRepository.findByCode("SAVE25").get();
        Coupon vip50 = couponRepository.findByCode("VIP50").get();

        User user = getUser("user@example.com");
        User admin = getUser("admin@example.com");

        if (couponAssignmentRepository.findByCouponIdAndUserId(welcome10.getId(), user.getId()).isEmpty()) {
            log.info("Seeding coupon assignments");
            couponAssignmentRepository.save(CouponAssignment.builder()
                    .coupon(welcome10)
                    .user(user)
                    .usedCount(0)
                    .build());
            couponAssignmentRepository.save(CouponAssignment.builder()
                    .coupon(save25)
                    .user(user)
                    .usedCount(0)
                    .build());
            couponAssignmentRepository.save(CouponAssignment.builder()
                    .coupon(vip50)
                    .user(admin)
                    .usedCount(0)
                    .build());
        }
    }

    private void seedDiscounts() {
        DiscountType percentage = getDiscountType("PERCENTAGE");
        DiscountType fixedAmount = getDiscountType("FIXED_AMOUNT");
        Instant now = Instant.now();
        Instant future = now.plus(Duration.ofDays(60));

        if (discountRepository.findByUuid("dummy-discount-1").isEmpty()) {
            log.info("Seeding discounts");

            Discount d1 = discountRepository.save(Discount.builder()
                    .discountType(percentage)
                    .discountValue(BigDecimal.valueOf(15))
                    .minOrderAmount(BigDecimal.valueOf(200))
                    .maxDiscount(BigDecimal.valueOf(100))
                    .isGlobal(true)
                    .isActive(true)
                    .validFrom(now)
                    .validUntil(future)
                    .description("15% off on orders over $200")
                    .build());

            Discount d2 = discountRepository.save(Discount.builder()
                    .discountType(fixedAmount)
                    .discountValue(BigDecimal.valueOf(75))
                    .minOrderAmount(BigDecimal.valueOf(300))
                    .isGlobal(false)
                    .isActive(true)
                    .validFrom(now)
                    .validUntil(future)
                    .description("$75 off on orders over $300")
                    .build());

            User user = getUser("user@example.com");

            if (discountAssignmentRepository.findByDiscountIdAndUserId(d2.getId(), user.getId()).isEmpty()) {
                discountAssignmentRepository.save(DiscountAssignment.builder()
                        .discount(d2)
                        .user(user)
                        .usedCount(0)
                        .build());
            }

            d1.setUuid("dummy-discount-1");
        }
    }

    private void seedOffers() {
        DiscountType percentage = getDiscountType("PERCENTAGE");
        Instant now = Instant.now();
        Instant future = now.plus(Duration.ofDays(45));

        if (!offerRepository.existsByTitle("Summer Sale - 20% Off Spirits")) {
            log.info("Seeding offers");

            Category spirits = categoryRepository.findBySlug("spirits").orElse(null);

            Offer o1 = offerRepository.save(Offer.builder()
                    .title("Summer Sale - 20% Off Spirits")
                    .description("Get 20% off on all spirits during our summer sale event")
                    .discountType(percentage)
                    .discountValue(BigDecimal.valueOf(20))
                    .maxDiscount(BigDecimal.valueOf(200))
                    .usageLimit(500)
                    .usageLimitPerUser(2)
                    .isGlobal(true)
                    .isActive(true)
                    .validFrom(now)
                    .validUntil(future)
                    .applicableTo("category")
                    .applicableIds(spirits != null ? String.valueOf(spirits.getId()) : "")
                    .build());

            Offer o2 = offerRepository.save(Offer.builder()
                    .title("Flash Deal - Buy 2 Get 1 Free")
                    .description("Buy any 2 items and get the 3rd one free (up to $50 value)")
                    .discountType(percentage)
                    .discountValue(BigDecimal.valueOf(100))
                    .maxDiscount(BigDecimal.valueOf(50))
                    .usageLimit(200)
                    .usageLimitPerUser(1)
                    .isGlobal(true)
                    .isActive(true)
                    .validFrom(now)
                    .validUntil(future)
                    .build());

            User user = getUser("user@example.com");

            if (offerAssignmentRepository.findByOfferIdAndUserId(o2.getId(), user.getId()).isEmpty()) {
                offerAssignmentRepository.save(OfferAssignment.builder()
                        .offer(o2)
                        .user(user)
                        .usedCount(0)
                        .build());
            }
        }
    }

    private void seedOrders() {
        Currency currency = getDefaultCurrency();
        OrderStatus pending = getOrderStatus("PENDING");
        OrderStatus processing = getOrderStatus("PROCESSING");
        OrderStatus delivered = getOrderStatus("DELIVERED");
        User user = getUser("user@example.com");
        User admin = getUser("admin@example.com");
        User superAdmin = getUser("superadmin@example.com");

        if (orderRepository.findByUserId(user.getId()).isEmpty()) {
            log.info("Seeding orders");

            Product jackDaniels = productRepository.findBySlug("jack-daniels-old-no-7").get();
            Product hendricks = productRepository.findBySlug("hendricks-gin").get();
            Product macallan12 = productRepository.findBySlug("macallan-12-year").get();
            Product absolutVodka = productRepository.findBySlug("absolut-vodka").get();

            ProductVariant jackVariant = productVariantRepository.findBySku("JD-ON7-750ML").get();
            ProductVariant macallanVariant = productVariantRepository.findBySku("MC-12-750ML").get();

            Coupon welcome10 = couponRepository.findByCode("WELCOME10").get();

            Order order1 = buildOrder(user, pending, currency, welcome10,
                    List.of(OrderItem.builder()
                                    .productId(jackDaniels.getId())
                                    .variantId(jackVariant.getId())
                                    .productName("Jack Daniel's Old No. 7")
                                    .variantName("Jack Daniel's Old No. 7 (750ml)")
                                    .sku(jackVariant.getSku())
                                    .quantity(1)
                                    .unitPrice(BigDecimal.valueOf(32.99))
                                    .totalPrice(BigDecimal.valueOf(32.99))
                                    .build(),
                            OrderItem.builder()
                                    .productId(hendricks.getId())
                                    .productName("Hendrick's Gin")
                                    .variantName("Hendrick's Gin (750ml)")
                                    .sku("HEN-750ML")
                                    .quantity(1)
                                    .unitPrice(BigDecimal.valueOf(39.99))
                                    .totalPrice(BigDecimal.valueOf(39.99))
                                    .build()),
                    List.of(OrderStatusHistory.builder()
                            .toStatus(pending)
                            .changedBy("SYSTEM")
                            .reason("Order placed")
                            .build()));
            order1.setSubtotal(BigDecimal.valueOf(72.98));
            order1.setDiscount(BigDecimal.valueOf(7.30));
            order1.setShippingCost(BigDecimal.valueOf(0));
            order1.setTax(BigDecimal.valueOf(5.84));
            order1.setTotal(BigDecimal.valueOf(71.52));
            orderRepository.save(order1);

            Order order2 = buildOrder(admin, delivered, currency, null,
                    List.of(OrderItem.builder()
                                    .productId(macallan12.getId())
                                    .variantId(macallanVariant.getId())
                                    .productName("The Macallan 12 Year Old")
                                    .variantName("The Macallan 12 Year Old (750ml)")
                                    .sku(macallanVariant.getSku())
                                    .quantity(2)
                                    .unitPrice(BigDecimal.valueOf(89.99))
                                    .totalPrice(BigDecimal.valueOf(179.98))
                                    .build()),
                    List.of(
                            OrderStatusHistory.builder()
                                    .toStatus(pending)
                                    .changedBy("SYSTEM")
                                    .reason("Order placed")
                                    .build(),
                            OrderStatusHistory.builder()
                                    .fromStatus(pending)
                                    .toStatus(processing)
                                    .changedBy("ADMIN")
                                    .reason("Payment confirmed")
                                    .build(),
                            OrderStatusHistory.builder()
                                    .fromStatus(processing)
                                    .toStatus(delivered)
                                    .changedBy("ADMIN")
                                    .reason("Delivered successfully")
                                    .build()));
            order2.setSubtotal(BigDecimal.valueOf(179.98));
            order2.setDiscount(BigDecimal.ZERO);
            order2.setShippingCost(BigDecimal.valueOf(9.99));
            order2.setTax(BigDecimal.valueOf(14.40));
            order2.setTotal(BigDecimal.valueOf(204.37));
            orderRepository.save(order2);

            Order order3 = buildOrder(superAdmin, processing, currency, null,
                    List.of(OrderItem.builder()
                                    .productId(absolutVodka.getId())
                                    .productName("Absolut Vodka")
                                    .variantName("Absolut Vodka (750ml)")
                                    .sku("ABS-750ML")
                                    .quantity(1)
                                    .unitPrice(BigDecimal.valueOf(24.99))
                                    .totalPrice(BigDecimal.valueOf(24.99))
                                    .build()),
                    List.of(
                            OrderStatusHistory.builder()
                                    .toStatus(pending)
                                    .changedBy("SYSTEM")
                                    .reason("Order placed")
                                    .build(),
                            OrderStatusHistory.builder()
                                    .fromStatus(pending)
                                    .toStatus(processing)
                                    .changedBy("ADMIN")
                                    .reason("Payment confirmed")
                                    .build()));
            order3.setSubtotal(BigDecimal.valueOf(24.99));
            order3.setDiscount(BigDecimal.ZERO);
            order3.setShippingCost(BigDecimal.valueOf(5.99));
            order3.setTax(BigDecimal.valueOf(2.00));
            order3.setTotal(BigDecimal.valueOf(32.98));
            orderRepository.save(order3);

            CouponAssignment assignment = couponAssignmentRepository
                    .findByCouponIdAndUserId(welcome10.getId(), user.getId()).get();
            assignment.setUsedCount(assignment.getUsedCount() + 1);
            couponAssignmentRepository.save(assignment);
        }
    }

    private Order buildOrder(User user, OrderStatus status, Currency currency, Coupon coupon,
                              List<OrderItem> items, List<OrderStatusHistory> history) {
        Order order = Order.builder()
                .user(user)
                .status(status)
                .currency(currency)
                .notes(coupon != null ? "Applied coupon: " + coupon.getCode() : null)
                .build();
        items.forEach(item -> item.setOrder(order));
        order.setItems(items);
        history.forEach(h -> h.setOrder(order));
        order.setStatusHistory(history);
        return order;
    }

    private void seedPayments() {
        PaymentGateway mock = getPaymentGateway("MOCK");
        PaymentStatus completed = getPaymentStatus("COMPLETED");
        Currency currency = getDefaultCurrency();

        List<Order> orders = orderRepository.findAll();
        for (Order order : orders) {
            if (paymentRepository.findByOrderId(order.getId()).isEmpty()) {
                log.info("Seeding payment for order {}", order.getOrderNumber());
                paymentRepository.save(Payment.builder()
                        .orderId(order.getId())
                        .user(order.getUser())
                        .gateway(mock)
                        .amount(order.getTotal())
                        .currency(currency)
                        .status(completed)
                        .method("CREDIT_CARD")
                        .gatewayTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                        .paidAt(Instant.now())
                        .build());
            }
        }
    }

    private void seedReviews() {
        User user = getUser("user@example.com");
        User admin = getUser("admin@example.com");

        Product jackDaniels = productRepository.findBySlug("jack-daniels-old-no-7").get();
        Product macallan12 = productRepository.findBySlug("macallan-12-year").get();
        Product absolutVodka = productRepository.findBySlug("absolut-vodka").get();
        Product hendricks = productRepository.findBySlug("hendricks-gin").get();
        Product bacardiSuperior = productRepository.findBySlug("bacardi-superior").get();
        Product greyGoose = productRepository.findBySlug("grey-goose-vodka").get();

        List<Review> existing = reviewRepository.findByProductId(jackDaniels.getId());
        if (existing.isEmpty()) {
            log.info("Seeding reviews");

            reviewRepository.save(Review.builder()
                    .product(jackDaniels)
                    .user(admin)
                    .rating(5)
                    .title("Smooth as silk")
                    .comment("Perfect balance of caramel and vanilla. My go-to pour for every evening.")
                    .isActive(true)
                    .isVerifiedPurchase(true)
                    .build());

            reviewRepository.save(Review.builder()
                    .product(jackDaniels)
                    .user(user)
                    .rating(4)
                    .title("Classic, but pricey")
                    .comment("Great Tennessee whiskey, though local stores sometimes have it cheaper.")
                    .isActive(true)
                    .isVerifiedPurchase(true)
                    .build());

            reviewRepository.save(Review.builder()
                    .product(macallan12)
                    .user(admin)
                    .rating(5)
                    .title("Worth every penny")
                    .comment("Dried fruit and sherry notes are stunning. A true single malt experience.")
                    .isActive(true)
                    .isVerifiedPurchase(true)
                    .build());

            reviewRepository.save(Review.builder()
                    .product(absolutVodka)
                    .user(user)
                    .rating(5)
                    .title("Crisp and clean")
                    .comment("Mixes beautifully in a martini and is smooth enough to sip neat.")
                    .isActive(true)
                    .isVerifiedPurchase(true)
                    .build());

            reviewRepository.save(Review.builder()
                    .product(hendricks)
                    .user(user)
                    .rating(5)
                    .title("Cucumber magic")
                    .comment("The cucumber and rose make it dangerously drinkable. Best served with tonic.")
                    .isActive(true)
                    .isVerifiedPurchase(true)
                    .build());

            reviewRepository.save(Review.builder()
                    .product(bacardiSuperior)
                    .user(admin)
                    .rating(4)
                    .title("Perfect for cocktails")
                    .comment("Light and clean. Makes a brilliant mojito or daiquiri.")
                    .isActive(true)
                    .isVerifiedPurchase(false)
                    .build());

            reviewRepository.save(Review.builder()
                    .product(greyGoose)
                    .user(user)
                    .rating(3)
                    .title("Good but overpriced")
                    .comment("Smooth vodka, but hard to justify the price over other premium options.")
                    .isActive(true)
                    .isVerifiedPurchase(false)
                    .build());
        }
    }

    private void seedCartItems() {
        User user = getUser("user@example.com");
        User admin = getUser("admin@example.com");

        Product johnnieWalker = productRepository.findBySlug("johnnie-walker-black-label").get();
        Product macallan18 = productRepository.findBySlug("macallan-18-year").get();
        Product belvedere = productRepository.findBySlug("belvedere-vodka").get();

        if (cartRepository.findByUserId(user.getId()).isEmpty()) {
            log.info("Seeding cart items");

            cartRepository.save(CartItem.builder()
                    .user(user)
                    .product(johnnieWalker)
                    .quantity(1)
                    .build());

            cartRepository.save(CartItem.builder()
                    .user(admin)
                    .product(macallan18)
                    .quantity(1)
                    .build());

            cartRepository.save(CartItem.builder()
                    .user(admin)
                    .product(belvedere)
                    .quantity(2)
                    .build());
        }
    }

    private void seedWishlistItems() {
        User user = getUser("user@example.com");
        User admin = getUser("admin@example.com");

        Product macallan18 = productRepository.findBySlug("macallan-18-year").get();
        Product veuveClicquot = productRepository.findBySlug("veuve-clicquot-yellow-label").get();
        Product donJulioAnejo = productRepository.findBySlug("don-julio-anejo").get();
        Product moetImperial = productRepository.findBySlug("moet-chandon-imperial").get();

        if (!wishlistRepository.existsByUserIdAndProductId(user.getId(), macallan18.getId())) {
            log.info("Seeding wishlist items");

            wishlistRepository.save(WishlistItem.builder()
                    .user(user)
                    .product(macallan18)
                    .build());

            wishlistRepository.save(WishlistItem.builder()
                    .user(user)
                    .product(veuveClicquot)
                    .build());

            wishlistRepository.save(WishlistItem.builder()
                    .user(admin)
                    .product(donJulioAnejo)
                    .build());

            wishlistRepository.save(WishlistItem.builder()
                    .user(admin)
                    .product(moetImperial)
                    .build());
        }
    }

    private void seedNotifications() {
        User user = getUser("user@example.com");
        User admin = getUser("admin@example.com");
        User superAdmin = getUser("superadmin@example.com");

        if (notificationRepository.countByUserIdAndIsReadFalse(user.getId()) == 0) {
            log.info("Seeding notifications");

            notificationRepository.save(Notification.builder()
                    .userId(user.getId())
                    .type("ORDER_CONFIRMED")
                    .title("Order Confirmed")
                    .body("Your order ORD-XXXX has been confirmed and is being processed.")
                    .deepLink("/orders/1")
                    .isRead(false)
                    .build());

            notificationRepository.save(Notification.builder()
                    .userId(user.getId())
                    .type("WELCOME")
                    .title("Welcome to Liquefied!")
                    .body("Thank you for joining. Enjoy 10% off your first purchase with code WELCOME10.")
                    .deepLink("/coupons")
                    .isRead(false)
                    .build());

            notificationRepository.save(Notification.builder()
                    .userId(admin.getId())
                    .type("NEW_ORDER")
                    .title("New Order Received")
                    .body("A new order has been placed and requires processing.")
                    .deepLink("/admin/orders")
                    .isRead(false)
                    .build());

            notificationRepository.save(Notification.builder()
                    .userId(superAdmin.getId())
                    .type("SYSTEM_UPDATE")
                    .title("System Update Available")
                    .body("A new system update is available. Please review the changelog.")
                    .deepLink("/admin/settings")
                    .isRead(false)
                    .build());

            notificationRepository.save(Notification.builder()
                    .userId(admin.getId())
                    .type("LOW_STOCK")
                    .title("Low Stock Alert")
                    .body("Product 'Johnnie Walker Blue Label' is running low on stock.")
                    .deepLink("/admin/products")
                    .isRead(false)
                    .build());
        }
    }

    private void seedAssistantFlows() {
        seedExtraCustomers();
        seedExtraOrders();
        seedReturns();
        seedChatData();
    }

    private void seedExtraCustomers() {
        if (userRepository.findByEmail("alice@example.com").isPresent()) {
            return;
        }
        log.info("Seeding extra customers and wallets");
        Role userRole = getRole("USER");
        String encoded = passwordEncoder.encode(PASSWORD);

        User alice = userRepository.save(User.builder()
                .firstName("Alice")
                .lastName("Johnson")
                .email("alice@example.com")
                .dialCode("+1")
                .phoneNumber("+1-555-0201")
                .password(encoded)
                .role(userRole)
                .isActive(true)
                .isEmailVerified(true)
                .isPhoneVerified(true)
                .address(makeAddress("11 Maple St", "Portland", "OR", "US", 97205L))
                .build());
        User bob = userRepository.save(User.builder()
                .firstName("Bob")
                .lastName("Smith")
                .email("bob@example.com")
                .dialCode("+1")
                .phoneNumber("+1-555-0202")
                .password(encoded)
                .role(userRole)
                .isActive(true)
                .isEmailVerified(true)
                .isPhoneVerified(false)
                .address(makeAddress("22 Oak Ave", "Denver", "CO", "US", 80202L))
                .build());
        User carol = userRepository.save(User.builder()
                .firstName("Carol")
                .lastName("Davis")
                .email("carol@example.com")
                .dialCode("+49")
                .phoneNumber("+49-555-0203")
                .password(encoded)
                .role(userRole)
                .isActive(true)
                .isEmailVerified(true)
                .isPhoneVerified(true)
                .address(makeAddress("33 Birch Ln", "Berlin", "BE", "DE", 10115L))
                .build());

        ensureWallet(alice, BigDecimal.valueOf(500));
        ensureWallet(bob, BigDecimal.valueOf(250));
        ensureWallet(carol, BigDecimal.valueOf(1250));
    }

    private void ensureWallet(User user, BigDecimal balance) {
        if (walletRepository.findByUserId(user.getId()).isPresent()) {
            return;
        }
        BigDecimal scaled = balance.setScale(4, RoundingMode.HALF_UP);
        Wallet wallet = walletRepository.save(Wallet.builder()
                .user(user)
                .balance(scaled)
                .currency(getDefaultCurrency())
                .isActive(true)
                .build());
        walletTransactionRepository.save(WalletTransaction.builder()
                .wallet(wallet)
                .type(getWalletTransactionType("CREDIT"))
                .amount(scaled)
                .balanceBefore(BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP))
                .balanceAfter(scaled)
                .referenceType("SIGNUP_BONUS")
                .description("Welcome bonus for account registration")
                .build());
    }

    private void seedExtraOrders() {
        User alice = getUser("alice@example.com");
        if (!orderRepository.findByUserId(alice.getId()).isEmpty()) {
            return;
        }
        log.info("Seeding extra orders with payments");
        Currency currency = getDefaultCurrency();
        OrderStatus pending = getOrderStatus("PENDING");
        OrderStatus confirmed = getOrderStatus("CONFIRMED");
        OrderStatus processing = getOrderStatus("PROCESSING");
        OrderStatus shipped = getOrderStatus("SHIPPED");
        OrderStatus delivered = getOrderStatus("DELIVERED");
        OrderStatus cancelled = getOrderStatus("CANCELLED");
        OrderStatus returnRequested = getOrderStatus("RETURN_REQUESTED");

        User bob = getUser("bob@example.com");
        User carol = getUser("carol@example.com");

        ProductVariant blueLabelStd = productVariantRepository.findBySku("JW-BLU-001-STD").get();
        ProductVariant jamesonStd = productVariantRepository.findBySku("JM-001-001-STD").get();
        ProductVariant patronStd = productVariantRepository.findBySku("PAT-SIL-001-STD").get();

        Order aliceOrder = buildOrder(alice, shipped, currency, null,
                List.of(orderItem(blueLabelStd, "Johnnie Walker Blue Label", new BigDecimal("189.99"), 1)),
                List.of(history(pending, confirmed, "SYSTEM", "Order placed"),
                        history(confirmed, processing, "ADMIN", "Payment confirmed"),
                        history(processing, shipped, "ADMIN", "Shipped via FedEx")));
        aliceOrder.setSubtotal(new BigDecimal("189.99"));
        aliceOrder.setDiscount(BigDecimal.ZERO);
        aliceOrder.setShippingCost(new BigDecimal("4.99"));
        aliceOrder.setTax(new BigDecimal("15.20"));
        aliceOrder.setTotal(new BigDecimal("210.18"));
        orderRepository.save(aliceOrder);

        Order bobOrder = buildOrder(bob, cancelled, currency, null,
                List.of(orderItem(jamesonStd, "Jameson Irish Whiskey", new BigDecimal("28.99"), 1)),
                List.of(history(pending, confirmed, "SYSTEM", "Order placed"),
                        history(confirmed, processing, "ADMIN", "Payment confirmed"),
                        history(processing, cancelled, "ADMIN", "Customer requested cancellation")));
        bobOrder.setSubtotal(new BigDecimal("28.99"));
        bobOrder.setDiscount(BigDecimal.ZERO);
        bobOrder.setShippingCost(new BigDecimal("4.99"));
        bobOrder.setTax(new BigDecimal("2.32"));
        bobOrder.setTotal(new BigDecimal("36.30"));
        bobOrder.setCanceledAt(Instant.now().minus(Duration.ofDays(2)));
        orderRepository.save(bobOrder);

        Order carolOrder = buildOrder(carol, returnRequested, currency, null,
                List.of(orderItem(patronStd, "Patrón Silver", new BigDecimal("49.99"), 1)),
                List.of(history(pending, confirmed, "SYSTEM", "Order placed"),
                        history(confirmed, processing, "ADMIN", "Payment confirmed"),
                        history(processing, shipped, "ADMIN", "Shipped via DHL"),
                        history(shipped, delivered, "ADMIN", "Delivered successfully"),
                        history(delivered, returnRequested, "USER", "Return requested by customer")));
        carolOrder.setSubtotal(new BigDecimal("49.99"));
        carolOrder.setDiscount(BigDecimal.ZERO);
        carolOrder.setShippingCost(BigDecimal.ZERO);
        carolOrder.setTax(new BigDecimal("4.00"));
        carolOrder.setTotal(new BigDecimal("53.99"));
        orderRepository.save(carolOrder);

        seedPayment(aliceOrder, "COMPLETED");
        seedPayment(bobOrder, "REFUNDED");
        seedPayment(carolOrder, "COMPLETED");
    }

    private OrderItem orderItem(ProductVariant variant, String productName, BigDecimal unitPrice, int quantity) {
        return OrderItem.builder()
                .productId(variant.getProduct().getId())
                .variantId(variant.getId())
                .productName(productName)
                .variantName(variant.getName())
                .sku(variant.getSku())
                .quantity(quantity)
                .unitPrice(unitPrice)
                .totalPrice(unitPrice.multiply(BigDecimal.valueOf(quantity)))
                .build();
    }

    private OrderStatusHistory history(OrderStatus from, OrderStatus to, String changedBy, String reason) {
        return OrderStatusHistory.builder()
                .fromStatus(from)
                .toStatus(to)
                .changedBy(changedBy)
                .reason(reason)
                .build();
    }

    private void seedPayment(Order order, String statusCode) {
        if (!paymentRepository.findByOrderId(order.getId()).isEmpty()) {
            return;
        }
        PaymentStatus status = getPaymentStatus(statusCode);
        Payment payment = Payment.builder()
                .orderId(order.getId())
                .user(order.getUser())
                .gateway(getPaymentGateway("MOCK"))
                .amount(order.getTotal())
                .currency(getDefaultCurrency())
                .status(status)
                .method("CREDIT_CARD")
                .gatewayTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .paidAt(Instant.now().minus(Duration.ofDays(3)))
                .build();
        paymentRepository.save(payment);
    }

    private void seedReturns() {
        if (!returnRequestRepository.findByUserId(getUser("carol@example.com").getId()).isEmpty()) {
            return;
        }
        log.info("Seeding return requests and refunds");

        User carol = getUser("carol@example.com");
        User alice = getUser("alice@example.com");
        User user = getUser("user@example.com");

        Order carolOrder = orderRepository.findByUserId(carol.getId()).get(0);
        Order aliceOrder = orderRepository.findByUserId(alice.getId()).get(0);
        Order userOrder = orderRepository.findByUserId(user.getId()).get(0);

        Payment carolPayment = paymentRepository.findByOrderId(carolOrder.getId()).get();
        Payment alicePayment = paymentRepository.findByOrderId(aliceOrder.getId()).get();

        ReturnRequest pendingReturn = returnRequestRepository.save(returnRequest(
                carol, carolOrder, "PENDING", "REFUND", "DEFECTIVE",
                "Bottle arrived with a leaky seal",
                null, null, null));

        returnItemRepository.save(returnItem(pendingReturn, carolOrder.getItems().get(0), "DEFECTIVE", 1));

        ReturnRequest approvedReturn = returnRequestRepository.save(returnRequest(
                alice, aliceOrder, "APPROVED", "STORE_CREDIT", "DAMAGED",
                "Bottle arrived with a chipped neck",
                "Approved after inspection - store credit issued",
                new BigDecimal("210.18"), null));

        returnItemRepository.save(returnItem(approvedReturn, aliceOrder.getItems().get(0), "DAMAGED", 1));

        Refund refund = Refund.builder()
                .payment(alicePayment)
                .returnRequestId(approvedReturn.getId())
                .amount(new BigDecimal("210.18"))
                .reason("Approved return - store credit")
                .status(getRefundStatus("COMPLETED"))
                .gatewayRefundId("TXN-REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .refundedAt(Instant.now().minus(Duration.ofDays(1)))
                .build();
        refundRepository.save(refund);

        ReturnRequest closedReturn = returnRequestRepository.save(returnRequest(
                user, userOrder, "CLOSED", "EXCHANGE", "NOT_AS_DESCRIBED",
                "Received the wrong color variant",
                "Exchange unavailable - request closed without refund",
                BigDecimal.ZERO, null));

        returnItemRepository.save(returnItem(closedReturn, userOrder.getItems().get(0), "NOT_AS_DESCRIBED", 1));

        if (paymentRepository.findByOrderId(carolOrder.getId()).isPresent()) {
            Refund pendingRefund = Refund.builder()
                    .payment(carolPayment)
                    .returnRequestId(pendingReturn.getId())
                    .amount(new BigDecimal("323.99"))
                    .reason("Pending return review")
                    .status(getRefundStatus("PENDING"))
                    .build();
            refundRepository.save(pendingRefund);
        }
    }

    private ReturnRequest returnRequest(User user, Order order, String statusCode, String typeCode,
                                        String conditionCode, String reason,
                                        String resolutionNotes, BigDecimal refundAmount, Instant createdAt) {
        ReturnRequest returnRequest = ReturnRequest.builder()
                .user(user)
                .order(order)
                .returnType(getReturnType(typeCode))
                .status(getReturnStatus(statusCode))
                .reason(reason)
                .resolutionNotes(resolutionNotes)
                .refundAmount(refundAmount)
                .isActive(true)
                .build();
        if (createdAt != null) {
            returnRequest.setCreatedAt(createdAt);
            returnRequest.setUpdatedAt(createdAt);
        }
        return returnRequest;
    }

    private ReturnItem returnItem(ReturnRequest returnRequest, OrderItem orderItem, String conditionCode, int quantity) {
        return ReturnItem.builder()
                .returnRequest(returnRequest)
                .orderItemId(orderItem.getId())
                .productId(orderItem.getProductId())
                .productName(orderItem.getProductName())
                .sku(orderItem.getSku())
                .quantity(quantity)
                .unitPrice(orderItem.getUnitPrice())
                .condition(getReturnCondition(conditionCode))
                .isActive(true)
                .build();
    }

    private void seedChatData() {
        if (!chatRoomRepository.findByUserId(getUser("alice@example.com").getId()).isEmpty()) {
            return;
        }
        log.info("Seeding chat rooms and messages");
        User alice = getUser("alice@example.com");
        User bob = getUser("bob@example.com");
        User carol = getUser("carol@example.com");
        User admin = getUser("admin@example.com");

        ChatRoom room1 = chatRoomRepository.save(ChatRoom.builder()
                .userId(alice.getId())
                .agentId(admin.getId())
                .status("ACTIVE")
                .topic("Johnnie Walker bottle damaged - return help")
                .createdAt(Instant.now().minus(Duration.ofDays(2)))
                .assignedAt(Instant.now().minus(Duration.ofDays(2)))
                .build());
        saveMessage(room1, "USER", alice.getId(), "Hi! My Johnnie Walker Black Label bottle arrived with a chipped neck.", true);
        saveMessage(room1, "AGENT", admin.getId(), "Sorry to hear that! Could you share a photo of the damage so we can log it?", true);
        saveMessage(room1, "AGENT", admin.getId(), "If that doesn't help we can arrange a return for you.", false);
        saveMessage(room1, "USER", alice.getId(), "Sent the photos. Can I get a replacement?", false);

        ChatRoom room2 = chatRoomRepository.save(ChatRoom.builder()
                .userId(bob.getId())
                .status("BOT_ACTIVE")
                .topic("Shipping times")
                .createdAt(Instant.now().minus(Duration.ofHours(5)))
                .build());
        saveMessage(room2, "USER", bob.getId(), "How long does standard shipping take?", true);
        saveMessage(room2, "AGENT", null, "Most orders ship within 1-2 business days. You can track delivery from your order page.", false);

        ChatRoom room3 = chatRoomRepository.save(ChatRoom.builder()
                .userId(carol.getId())
                .agentId(admin.getId())
                .status("CLOSED")
                .topic("Return request tracking")
                .createdAt(Instant.now().minus(Duration.ofDays(6)))
                .assignedAt(Instant.now().minus(Duration.ofDays(6)))
                .closedAt(Instant.now().minus(Duration.ofDays(3)))
                .build());
        saveMessage(room3, "USER", carol.getId(), "I submitted a return yesterday. When will it be processed?", true);
        saveMessage(room3, "AGENT", admin.getId(), "Our team will review it within 24-48 hours. You will get an email once approved.", true);
        saveMessage(room3, "USER", carol.getId(), "Thank you!", true);
        saveMessage(room3, "AGENT", admin.getId(), "You're welcome! The ticket is now closed.", true);
    }

    private void saveMessage(ChatRoom room, String senderType, Long senderId, String content, boolean read) {
        ChatMessage message = ChatMessage.builder()
                .roomId(room.getId())
                .senderType(senderType)
                .senderId(senderId)
                .content(content)
                .messageType("TEXT")
                .build();
        if (read) {
            message.setReadAt(Instant.now());
        }
        chatMessageRepository.save(message);
    }

    private ReturnStatus getReturnStatus(String code) {
        return returnStatusRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Return status " + code + " not found after seeding"));
    }

    private ReturnType getReturnType(String code) {
        return returnTypeRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Return type " + code + " not found after seeding"));
    }

    private ReturnCondition getReturnCondition(String code) {
        return returnConditionRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Return condition " + code + " not found after seeding"));
    }

    private RefundStatus getRefundStatus(String code) {
        return refundStatusRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Refund status " + code + " not found after seeding"));
    }
}
