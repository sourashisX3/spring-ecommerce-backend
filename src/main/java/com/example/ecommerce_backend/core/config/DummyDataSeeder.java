package com.example.ecommerce_backend.core.config;

import com.example.ecommerce_backend.modules.brand.entity.Brand;
import com.example.ecommerce_backend.modules.brand.repository.BrandRepository;
import com.example.ecommerce_backend.modules.cart.entity.CartItem;
import com.example.ecommerce_backend.modules.cart.repository.CartRepository;
import com.example.ecommerce_backend.modules.category.entity.Category;
import com.example.ecommerce_backend.modules.category.repository.CategoryRepository;
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
import com.example.ecommerce_backend.modules.product.entity.Product;
import com.example.ecommerce_backend.modules.product.repository.ProductRepository;
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
                           NotificationRepository notificationRepository) {
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
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        ensureSeedUserPasswords();

        if (userRepository.findByEmail("superadmin@example.com").isPresent()) {
            log.info("Dummy data already exists, refreshing catalog");
            upgradeLegacyProductImages();
            seedBulkCatalog();
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
        upgradeLegacyProductImages();
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

    private Role getRole(String name) {
        return rolesRepository.findByRoleName(name)
                .orElseThrow(() -> new RuntimeException("Role " + name + " not found"));
    }

    private Currency getCurrency(String code) {
        return currencyRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Currency " + code + " not found"));
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
        Currency usd = getCurrency("USD");

        for (String email : List.of("superadmin@example.com", "admin@example.com", "user@example.com")) {
            User user = getUser(email);
            if (walletRepository.findByUserId(user.getId()).isEmpty()) {
                log.info("Seeding wallet for {}", email);
                walletRepository.save(Wallet.builder()
                        .user(user)
                        .balance(BigDecimal.valueOf(10000).setScale(4, RoundingMode.HALF_UP))
                        .currency(usd)
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
        if (categoryRepository.findBySlug("electronics").isEmpty()) {
            log.info("Seeding categories");

            Category electronics = categoryRepository.save(Category.builder()
                    .name("Electronics")
                    .slug("electronics")
                    .description("Electronic devices and accessories")
                    .sortOrder(1)
                    .isActive(true)
                    .build());

            Category clothing = categoryRepository.save(Category.builder()
                    .name("Clothing")
                    .slug("clothing")
                    .description("Apparel and fashion accessories")
                    .sortOrder(2)
                    .isActive(true)
                    .build());

            Category homeKitchen = categoryRepository.save(Category.builder()
                    .name("Home & Kitchen")
                    .slug("home-kitchen")
                    .description("Home appliances and kitchen essentials")
                    .sortOrder(3)
                    .isActive(true)
                    .build());

            categoryRepository.save(Category.builder()
                    .name("Mobile Phones")
                    .slug("mobile-phones")
                    .description("Smartphones and accessories")
                    .parent(electronics)
                    .sortOrder(1)
                    .isActive(true)
                    .build());

            categoryRepository.save(Category.builder()
                    .name("Laptops")
                    .slug("laptops")
                    .description("Notebooks and ultrabooks")
                    .parent(electronics)
                    .sortOrder(2)
                    .isActive(true)
                    .build());

            categoryRepository.save(Category.builder()
                    .name("Headphones")
                    .slug("headphones")
                    .description("Headphones and earphones")
                    .parent(electronics)
                    .sortOrder(3)
                    .isActive(true)
                    .build());

            categoryRepository.save(Category.builder()
                    .name("Men's Clothing")
                    .slug("mens-clothing")
                    .description("Men's fashion and apparel")
                    .parent(clothing)
                    .sortOrder(1)
                    .isActive(true)
                    .build());

            categoryRepository.save(Category.builder()
                    .name("Women's Clothing")
                    .slug("womens-clothing")
                    .description("Women's fashion and apparel")
                    .parent(clothing)
                    .sortOrder(2)
                    .isActive(true)
                    .build());
        }
    }

    private Category getCategory(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Category " + slug + " not found"));
    }

    private void seedBrands() {
        Map<String, String[]> brands = new LinkedHashMap<>();
        brands.put("apple", new String[]{"Apple", "Premium consumer electronics", "https://www.apple.com"});
        brands.put("samsung", new String[]{"Samsung", "Consumer electronics and appliances", "https://www.samsung.com"});
        brands.put("sony", new String[]{"Sony", "Electronics, gaming and entertainment", "https://www.sony.com"});
        brands.put("nike", new String[]{"Nike", "Athletic footwear and apparel", "https://www.nike.com"});
        brands.put("adidas", new String[]{"Adidas", "Sportswear and accessories", "https://www.adidas.com"});
        brands.put("dell", new String[]{"Dell", "Computer technology products", "https://www.dell.com"});
        brands.put("lg", new String[]{"LG", "Home appliances and electronics", "https://www.lg.com"});

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

    private void upgradeLegacyProductImages() {
        String[] slugs = {
                "iphone-16-pro-max",
                "samsung-galaxy-s25-ultra",
                "macbook-air-m4",
                "dell-xps-16",
                "sony-wh-1000xm6",
                "nike-air-max-270",
                "adidas-ultraboost-25",
                "lg-oled-evo-c5-65",
        };
        for (String slug : slugs) {
            productRepository.findBySlug(slug).ifPresent(product -> {
                List<ProductImage> images = productImageRepository.findByProductId(product.getId());
                images.sort(Comparator.comparingInt(ProductImage::getSortOrder));
                for (int i = 0; i < images.size(); i++) {
                    if (images.get(i).getImageUrl().contains("placehold.co")) {
                        images.get(i).setImageUrl(
                                "http://localhost:8083/api/v1/images/products/" + slug + "-" + (i + 1) + ".svg");
                        productImageRepository.save(images.get(i));
                    }
                }
            });
        }
    }

    private void seedBulkCatalog() {
        if (productRepository.findBySlug("apple-airpods-pro-3").isPresent()) {
            return;
        }
        log.info("Seeding bulk catalog ({} products)", BULK_PRODUCTS.size());

        Tag bestSeller = getTag("best-seller");
        Tag sale = getTag("sale");
        Tag featured = getTag("featured");
        Tag trending = getTag("trending");

        for (int i = 0; i < BULK_PRODUCTS.size(); i++) {
            BulkProduct b = BULK_PRODUCTS.get(i);
            Product product = productRepository.save(Product.builder()
                    .sku(b.sku())
                    .name(b.name())
                    .slug(b.slug())
                    .description("Premium " + b.name() + " crafted for everyday use. Durable build, tested quality, ready to ship.")
                    .shortDescription("Popular " + b.name() + " selection")
                    .basePrice(BigDecimal.valueOf(b.price()))
                    .category(getCategory(b.category()))
                    .brand(getBrand(b.brand()))
                    .tags(i % 3 == 0 ? Set.of(bestSeller, trending) : i % 4 == 0 ? Set.of(featured, sale) : Set.of(sale))
                    .isActive(b.active())
                    .isFeatured(i % 5 == 0)
                    .attributes(Map.of("color", b.color(), "model", b.name()))
                    .build());

            productVariantRepository.save(ProductVariant.builder()
                    .sku(b.sku() + "-STD")
                    .name(b.name() + " (Standard)")
                    .price(BigDecimal.valueOf(b.price()))
                    .stock(b.stock())
                    .product(product)
                    .isActive(b.active())
                    .isDefault(true)
                    .sortOrder(0)
                    .attributes(Map.of("configuration", "Standard"))
                    .build());

            seedProductImages(product, b.slug(), List.of(
                    "http://localhost:8083/api/v1/images/products/" + b.slug() + "-1.svg",
                    "http://localhost:8083/api/v1/images/products/" + b.slug() + "-2.svg"));
        }
    }

    private record BulkProduct(String slug, String name, String sku, double price,
                               String category, String brand, String color, int stock, boolean active) {
    }

    private static final List<BulkProduct> BULK_PRODUCTS = List.of(
            new BulkProduct("apple-airpods-pro-3", "Apple AirPods Pro 3", "APL-APP3-001", 249.99, "headphones", "apple", "White", 32, true),
            new BulkProduct("samsung-galaxy-buds3-pro", "Samsung Galaxy Buds3 Pro", "SAM-GB3P-001", 229.99, "headphones", "samsung", "Silver", 28, true),
            new BulkProduct("sony-wf-1000xm7", "Sony WF-1000XM7", "SONY-WF10-001", 299.99, "headphones", "sony", "Black", 22, true),
            new BulkProduct("sony-wh-ch720n", "Sony WH-CH720N", "SONY-CH720-001", 149.99, "headphones", "sony", "Black", 18, true),
            new BulkProduct("sony-ult-earbuds", "Sony ULT Earbuds", "SONY-ULT-001", 199.99, "headphones", "sony", "Blue", 15, false),
            new BulkProduct("apple-iphone-16", "Apple iPhone 16", "APL-IP16-001", 799.99, "mobile-phones", "apple", "Black", 26, true),
            new BulkProduct("apple-iphone-16-pro", "Apple iPhone 16 Pro", "APL-IP16P-001", 999.99, "mobile-phones", "apple", "Desert Titanium", 20, true),
            new BulkProduct("samsung-galaxy-s25", "Samsung Galaxy S25", "SAM-GS25-001", 799.99, "mobile-phones", "samsung", "Icy Blue", 24, true),
            new BulkProduct("samsung-galaxy-z-flip-7", "Samsung Galaxy Z Flip 7", "SAM-ZFL7-001", 1099.99, "mobile-phones", "samsung", "Silver Shadow", 12, false),
            new BulkProduct("apple-macbook-pro-16-m4", "Apple MacBook Pro 16\" M4", "APL-MBP16-001", 2499.99, "laptops", "apple", "Space Black", 9, true),
            new BulkProduct("apple-macbook-air-15-m4", "Apple MacBook Air 15\" M4", "APL-MBA15-001", 1299.99, "laptops", "apple", "Starlight", 14, true),
            new BulkProduct("dell-xps-13", "Dell XPS 13", "DELL-XPS13-001", 1099.99, "laptops", "dell", "Platinum", 16, true),
            new BulkProduct("dell-inspiron-15", "Dell Inspiron 15", "DELL-INSP15-001", 699.99, "laptops", "dell", "Silver", 21, true),
            new BulkProduct("dell-alienware-m16", "Dell Alienware M16", "DELL-AW16-001", 1999.99, "laptops", "dell", "Legendary White", 8, false),
            new BulkProduct("lg-gram-17", "LG Gram 17", "LG-GRAM17-001", 1499.99, "laptops", "lg", "Black", 10, true),
            new BulkProduct("lg-55-oled-evo-c5", "LG 55\" OLED evo C5", "LG-OLED55-001", 1399.99, "electronics", "lg", "Black", 12, true),
            new BulkProduct("lg-cinebeam-q", "LG CineBeam Q Projector", "LG-CBQ-001", 799.99, "electronics", "lg", "White", 6, true),
            new BulkProduct("samsung-75-neo-qled", "Samsung 75\" Neo QLED", "SAM-75NQL-001", 1999.99, "electronics", "samsung", "Black", 7, true),
            new BulkProduct("samsung-32-m8-monitor", "Samsung Smart Monitor M8", "SAM-M8-001", 599.99, "electronics", "samsung", "Spring Green", 13, false),
            new BulkProduct("lg-soundbar-s90tr", "LG Soundbar S90TR", "LG-SB90-001", 499.99, "electronics", "lg", "Black", 11, true),
            new BulkProduct("lg-ultragear-27", "LG UltraGear 27\" 360Hz", "LG-UG27-001", 699.99, "electronics", "lg", "Black/Red", 9, true),
            new BulkProduct("nike-dri-fit-tee", "Nike Dri-FIT Running Tee", "NKE-DFT-001", 34.99, "mens-clothing", "nike", "Black", 45, true),
            new BulkProduct("nike-air-force-1", "Nike Air Force 1", "NKE-AF1-001", 109.99, "mens-clothing", "nike", "White", 30, true),
            new BulkProduct("nike-dunk-low", "Nike Dunk Low", "NKE-DL-001", 109.99, "mens-clothing", "nike", "University Red", 27, true),
            new BulkProduct("nike-pegasus-41", "Nike Pegasus 41", "NKE-PG41-001", 129.99, "mens-clothing", "nike", "Black/White", 25, true),
            new BulkProduct("nike-react-infinity", "Nike React Infinity Run", "NKE-RIR-001", 159.99, "mens-clothing", "nike", "Multi", 19, false),
            new BulkProduct("adidas-stan-smith", "Adidas Stan Smith", "ADI-SS-001", 99.99, "mens-clothing", "adidas", "White/Green", 34, true),
            new BulkProduct("adidas-samba-og", "Adidas Samba OG", "ADI-SAM-001", 99.99, "mens-clothing", "adidas", "Black/White", 31, true),
            new BulkProduct("adidas-nmd-r1", "Adidas NMD R1", "ADI-NMD-001", 139.99, "mens-clothing", "adidas", "Grey", 23, true),
            new BulkProduct("adidas-tiro-24-jacket", "Adidas Tiro 24 Track Jacket", "ADI-T24-001", 64.99, "mens-clothing", "adidas", "Navy", 38, true),
            new BulkProduct("nike-waffle-one", "Nike Waffle One", "NKE-WO-001", 99.99, "womens-clothing", "nike", "Sesame", 29, true),
            new BulkProduct("nike-pro-sports-bra", "Nike Pro Sports Bra", "NKE-PSB-001", 49.99, "womens-clothing", "nike", "White", 42, true),
            new BulkProduct("nike-windrunner-jacket", "Nike Windrunner Jacket", "NKE-WRJ-001", 89.99, "womens-clothing", "nike", "Black", 24, true),
            new BulkProduct("adidas-superstar", "Adidas Superstar", "ADI-SS-002", 99.99, "womens-clothing", "adidas", "White/Black", 33, true),
            new BulkProduct("adidas-essentials-tee", "Adidas Essentials 3-Stripes Tee", "ADI-EST-001", 29.99, "womens-clothing", "adidas", "Pink", 48, true),
            new BulkProduct("adidas-forum-low", "Adidas Forum Low", "ADI-FL-001", 109.99, "womens-clothing", "adidas", "Cloud White", 26, true),
            new BulkProduct("adidas-ultraboost-light", "Adidas Ultraboost Light", "ADI-UBL-001", 169.99, "womens-clothing", "adidas", "Core Black", 21, false),
            new BulkProduct("lg-air-fryer-42", "LG Air Fryer 4.2L", "LG-AF42-001", 129.99, "home-kitchen", "lg", "Graphite", 17, true),
            new BulkProduct("lg-cordzero-vacuum", "LG CordZero Stick Vacuum", "LG-CZV-001", 399.99, "home-kitchen", "lg", "Silver", 12, true),
            new BulkProduct("lg-puricare-purifier", "LG PuriCare Air Purifier", "LG-PC-001", 249.99, "home-kitchen", "lg", "White", 14, true));

    private void seedProducts() {
        Category mobilePhones = getCategory("mobile-phones");
        Category laptops = getCategory("laptops");
        Category headphones = getCategory("headphones");
        Category mensClothing = getCategory("mens-clothing");
        Category womensClothing = getCategory("womens-clothing");
        Category electronics = getCategory("electronics");

        Brand apple = getBrand("apple");
        Brand samsung = getBrand("samsung");
        Brand sony = getBrand("sony");
        Brand nike = getBrand("nike");
        Brand adidas = getBrand("adidas");
        Brand dell = getBrand("dell");
        Brand lg = getBrand("lg");

        Tag newArrival = getTag("new-arrival");
        Tag bestSeller = getTag("best-seller");
        Tag sale = getTag("sale");
        Tag featured = getTag("featured");
        Tag trending = getTag("trending");
        Tag limitedEdition = getTag("limited-edition");

        if (productRepository.findBySlug("iphone-16-pro-max").isEmpty()) {
            log.info("Seeding products");

            Product iphone = productRepository.save(Product.builder()
                    .sku("APL-IP16PM-001")
                    .name("iPhone 16 Pro Max")
                    .slug("iphone-16-pro-max")
                    .description("The most powerful iPhone ever. A18 Pro chip, 48MP camera system, titanium design.")
                    .shortDescription("Apple's flagship smartphone with A18 Pro chip")
                    .basePrice(BigDecimal.valueOf(1199.99))
                    .category(mobilePhones)
                    .brand(apple)
                    .tags(Set.of(newArrival, bestSeller, featured))
                    .isActive(true)
                    .isFeatured(true)
                    .attributes(Map.of("color", "Natural Titanium", "display", "6.9-inch OLED"))
                    .build());

            Product galaxy = productRepository.save(Product.builder()
                    .sku("SAM-GS25U-001")
                    .name("Samsung Galaxy S25 Ultra")
                    .slug("samsung-galaxy-s25-ultra")
                    .description("Galaxy AI is here. The ultimate Galaxy experience with S Pen and 200MP camera.")
                    .shortDescription("Samsung's premium flagship with built-in S Pen")
                    .basePrice(BigDecimal.valueOf(1299.99))
                    .category(mobilePhones)
                    .brand(samsung)
                    .tags(Set.of(newArrival, trending, featured))
                    .isActive(true)
                    .isFeatured(true)
                    .attributes(Map.of("color", "Titanium Gray", "display", "6.9-inch Dynamic AMOLED"))
                    .build());

            Product macbook = productRepository.save(Product.builder()
                    .sku("APL-MBA-M4-001")
                    .name("MacBook Air M4")
                    .slug("macbook-air-m4")
                    .description("Supercharged by M4 chip. Built for Apple Intelligence. Remarkably thin and ready for anything.")
                    .shortDescription("Apple's thinnest laptop with M4 chip")
                    .basePrice(BigDecimal.valueOf(1099.99))
                    .category(laptops)
                    .brand(apple)
                    .tags(Set.of(bestSeller, featured))
                    .isActive(true)
                    .isFeatured(true)
                    .attributes(Map.of("color", "Midnight", "display", "13.6-inch Liquid Retina"))
                    .build());

            Product dellXps = productRepository.save(Product.builder()
                    .sku("DELL-XPS16-001")
                    .name("Dell XPS 16")
                    .slug("dell-xps-16")
                    .description("Stunning 4K OLED display. Intel Core Ultra processor. Premium aluminum build.")
                    .shortDescription("Premium Windows laptop with 4K OLED display")
                    .basePrice(BigDecimal.valueOf(1499.99))
                    .category(laptops)
                    .brand(dell)
                    .tags(Set.of(newArrival, trending))
                    .isActive(true)
                    .isFeatured(false)
                    .attributes(Map.of("color", "Platinum Silver", "display", "16-inch 4K OLED"))
                    .build());

            Product sonyHeadphones = productRepository.save(Product.builder()
                    .sku("SONY-WH1000XM6-001")
                    .name("Sony WH-1000XM6")
                    .slug("sony-wh-1000xm6")
                    .description("Industry-leading noise cancellation. Crystal-clear hands-free calling. 30-hour battery life.")
                    .shortDescription("Premium wireless noise-cancelling headphones")
                    .basePrice(BigDecimal.valueOf(349.99))
                    .category(headphones)
                    .brand(sony)
                    .tags(Set.of(bestSeller, featured, sale))
                    .isActive(true)
                    .isFeatured(true)
                    .attributes(Map.of("color", "Black", "type", "Over-Ear"))
                    .build());

            Product nikeShoes = productRepository.save(Product.builder()
                    .sku("NKE-AM270-001")
                    .name("Nike Air Max 270")
                    .slug("nike-air-max-270")
                    .description("The Nike Air Max 270 delivers visible cushioning under every step. Giant Air unit provides unmatched comfort.")
                    .shortDescription("Iconic lifestyle sneaker with giant Air unit")
                    .basePrice(BigDecimal.valueOf(149.99))
                    .category(mensClothing)
                    .brand(nike)
                    .tags(Set.of(bestSeller, trending))
                    .isActive(true)
                    .isFeatured(true)
                    .attributes(Map.of("color", "Black/White", "type", "Sneakers"))
                    .build());

            Product adidasShoes = productRepository.save(Product.builder()
                    .sku("ADI-UB25-001")
                    .name("Adidas Ultraboost 25")
                    .slug("adidas-ultraboost-25")
                    .description("The most responsive Ultraboost yet. Lightstrike Pro cushioning meets Primeknit upper.")
                    .shortDescription("Premium running shoes with Lightstrike Pro")
                    .basePrice(BigDecimal.valueOf(189.99))
                    .category(womensClothing)
                    .brand(adidas)
                    .tags(Set.of(newArrival, featured))
                    .isActive(true)
                    .isFeatured(false)
                    .attributes(Map.of("color", "Core Black/White", "type", "Running Shoes"))
                    .build());

            Product lgTV = productRepository.save(Product.builder()
                    .sku("LG-OLED65-001")
                    .name("LG 65\" OLED evo C5")
                    .slug("lg-oled-evo-c5-65")
                    .description("Self-lit OLED evo display. Dolby Vision and Dolby Atmos. α11 AI processor.")
                    .shortDescription("65-inch 4K OLED smart TV with Dolby Atmos")
                    .basePrice(BigDecimal.valueOf(1799.99))
                    .category(electronics)
                    .brand(lg)
                    .tags(Set.of(featured, sale, limitedEdition))
                    .isActive(true)
                    .isFeatured(true)
                    .attributes(Map.of("screenSize", "65-inch", "resolution", "4K OLED", "smartTV", "webOS"))
                    .build());

            seedVariantsAndImages(iphone, galaxy, macbook, dellXps, sonyHeadphones, nikeShoes, adidasShoes, lgTV);
        }
    }

    private void seedVariantsAndImages(Product iphone, Product galaxy, Product macbook, Product dellXps,
                                        Product sonyHeadphones, Product nikeShoes, Product adidasShoes, Product lgTV) {
        log.info("Seeding variants and images");

        seedPhoneVariants(iphone, "APL-IP16PM", "iPhone 16 Pro Max",
                List.of("256GB", "512GB", "1TB"),
                List.of(BigDecimal.ZERO, BigDecimal.valueOf(200), BigDecimal.valueOf(400)),
                List.of(50, 30, 15));
        seedPhoneVariants(galaxy, "SAM-GS25U", "Samsung Galaxy S25 Ultra",
                List.of("256GB", "512GB", "1TB"),
                List.of(BigDecimal.ZERO, BigDecimal.valueOf(180), BigDecimal.valueOf(360)),
                List.of(40, 25, 10));

        seedLaptopVariants(macbook, "APL-MBA-M4", "MacBook Air M4",
                List.of("16GB/256GB", "24GB/512GB"),
                List.of(BigDecimal.ZERO, BigDecimal.valueOf(300)),
                List.of(25, 15));
        seedLaptopVariants(dellXps, "DELL-XPS16", "Dell XPS 16",
                List.of("16GB/512GB", "32GB/1TB"),
                List.of(BigDecimal.ZERO, BigDecimal.valueOf(400)),
                List.of(20, 10));

        seedHeadphoneVariants(sonyHeadphones, "SONY-WH1000XM6", "Sony WH-1000XM6",
                List.of("Black", "Silver", "Midnight Blue"),
                List.of(30, 15, 10));

        seedShoeVariants(nikeShoes, "NKE-AM270", "Nike Air Max 270",
                List.of("US 8", "US 9", "US 10", "US 11"),
                List.of(25, 35, 30, 20));
        seedShoeVariants(adidasShoes, "ADI-UB25", "Adidas Ultraboost 25",
                List.of("US 6", "US 7", "US 8", "US 9"),
                List.of(20, 25, 25, 15));

        seedTVVariants(lgTV, "LG-OLED65", "LG 65\" OLED evo C5",
                List.of("65-inch", "77-inch"),
                List.of(BigDecimal.ZERO, BigDecimal.valueOf(1200)),
                List.of(10, 5));

        seedProductImages(iphone, "iPhone-16-Pro-Max", List.of(
                "http://localhost:8083/api/v1/images/products/iphone-16-pro-max-1.svg",
                "http://localhost:8083/api/v1/images/products/iphone-16-pro-max-2.svg"));
        seedProductImages(galaxy, "Galaxy-S25-Ultra", List.of(
                "http://localhost:8083/api/v1/images/products/samsung-galaxy-s25-ultra-1.svg",
                "http://localhost:8083/api/v1/images/products/samsung-galaxy-s25-ultra-2.svg"));
        seedProductImages(macbook, "MacBook-Air-M4", List.of(
                "http://localhost:8083/api/v1/images/products/macbook-air-m4-1.svg",
                "http://localhost:8083/api/v1/images/products/macbook-air-m4-2.svg"));
        seedProductImages(dellXps, "Dell-XPS-16", List.of(
                "http://localhost:8083/api/v1/images/products/dell-xps-16-1.svg",
                "http://localhost:8083/api/v1/images/products/dell-xps-16-2.svg"));
        seedProductImages(sonyHeadphones, "Sony-WH1000XM6", List.of(
                "http://localhost:8083/api/v1/images/products/sony-wh-1000xm6-1.svg",
                "http://localhost:8083/api/v1/images/products/sony-wh-1000xm6-2.svg"));
        seedProductImages(nikeShoes, "Nike-Air-Max-270", List.of(
                "http://localhost:8083/api/v1/images/products/nike-air-max-270-1.svg",
                "http://localhost:8083/api/v1/images/products/nike-air-max-270-2.svg"));
        seedProductImages(adidasShoes, "Adidas-Ultraboost-25", List.of(
                "http://localhost:8083/api/v1/images/products/adidas-ultraboost-25-1.svg",
                "http://localhost:8083/api/v1/images/products/adidas-ultraboost-25-2.svg"));
        seedProductImages(lgTV, "LG-OLED-C5", List.of(
                "http://localhost:8083/api/v1/images/products/lg-oled-evo-c5-65-1.svg",
                "http://localhost:8083/api/v1/images/products/lg-oled-evo-c5-65-2.svg"));
    }

    private void seedPhoneVariants(Product product, String skuPrefix, String baseName,
                                    List<String> storages, List<BigDecimal> priceAdjustments, List<Integer> stocks) {
        for (int i = 0; i < storages.size(); i++) {
            boolean isDefault = i == 0;
            BigDecimal price = product.getBasePrice().add(priceAdjustments.get(i));
            productVariantRepository.save(ProductVariant.builder()
                    .sku(skuPrefix + "-" + storages.get(i).replace("/", "-"))
                    .name(baseName + " (" + storages.get(i) + ")")
                    .price(price)
                    .stock(stocks.get(i))
                    .product(product)
                    .isActive(true)
                    .isDefault(isDefault)
                    .sortOrder(i)
                    .attributes(Map.of("storage", storages.get(i)))
                    .build());
        }
    }

    private void seedLaptopVariants(Product product, String skuPrefix, String baseName,
                                     List<String> configs, List<BigDecimal> priceAdjustments, List<Integer> stocks) {
        for (int i = 0; i < configs.size(); i++) {
            boolean isDefault = i == 0;
            BigDecimal price = product.getBasePrice().add(priceAdjustments.get(i));
            String configLabel = configs.get(i).replace("/", "-").replace(" ", "");
            productVariantRepository.save(ProductVariant.builder()
                    .sku(skuPrefix + "-" + configLabel)
                    .name(baseName + " (" + configs.get(i) + ")")
                    .price(price)
                    .stock(stocks.get(i))
                    .product(product)
                    .isActive(true)
                    .isDefault(isDefault)
                    .sortOrder(i)
                    .attributes(Map.of("configuration", configs.get(i)))
                    .build());
        }
    }

    private void seedHeadphoneVariants(Product product, String skuPrefix, String baseName,
                                        List<String> colors, List<Integer> stocks) {
        for (int i = 0; i < colors.size(); i++) {
            boolean isDefault = i == 0;
            productVariantRepository.save(ProductVariant.builder()
                    .sku(skuPrefix + "-" + colors.get(i).replace(" ", "-"))
                    .name(baseName + " (" + colors.get(i) + ")")
                    .price(product.getBasePrice())
                    .stock(stocks.get(i))
                    .product(product)
                    .isActive(true)
                    .isDefault(isDefault)
                    .sortOrder(i)
                    .attributes(Map.of("color", colors.get(i)))
                    .build());
        }
    }

    private void seedShoeVariants(Product product, String skuPrefix, String baseName,
                                   List<String> sizes, List<Integer> stocks) {
        for (int i = 0; i < sizes.size(); i++) {
            boolean isDefault = i == 1;
            String sizeLabel = sizes.get(i).replace(" ", "");
            productVariantRepository.save(ProductVariant.builder()
                    .sku(skuPrefix + "-" + sizeLabel)
                    .name(baseName + " (Size " + sizes.get(i) + ")")
                    .price(product.getBasePrice())
                    .stock(stocks.get(i))
                    .product(product)
                    .isActive(true)
                    .isDefault(isDefault)
                    .sortOrder(i)
                    .attributes(Map.of("size", sizes.get(i)))
                    .build());
        }
    }

    private void seedTVVariants(Product product, String skuPrefix, String baseName,
                                 List<String> sizes, List<BigDecimal> priceAdjustments, List<Integer> stocks) {
        for (int i = 0; i < sizes.size(); i++) {
            boolean isDefault = i == 0;
            BigDecimal price = product.getBasePrice().add(priceAdjustments.get(i));
            productVariantRepository.save(ProductVariant.builder()
                    .sku(skuPrefix + "-" + sizes.get(i).replace("\"", "inch").replace("-", ""))
                    .name(baseName + " (" + sizes.get(i) + ")")
                    .price(price)
                    .stock(stocks.get(i))
                    .product(product)
                    .isActive(true)
                    .isDefault(isDefault)
                    .sortOrder(i)
                    .attributes(Map.of("screenSize", sizes.get(i)))
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
                    .description("$50 off premium electronics")
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

        if (!offerRepository.existsByTitle("Summer Sale - 20% Off Electronics")) {
            log.info("Seeding offers");

            Offer o1 = offerRepository.save(Offer.builder()
                    .title("Summer Sale - 20% Off Electronics")
                    .description("Get 20% off on all electronics during our summer sale event")
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
                    .applicableIds("1")
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
        Currency usd = getCurrency("USD");
        OrderStatus pending = getOrderStatus("PENDING");
        OrderStatus processing = getOrderStatus("PROCESSING");
        OrderStatus delivered = getOrderStatus("DELIVERED");
        User user = getUser("user@example.com");
        User admin = getUser("admin@example.com");
        User superAdmin = getUser("superadmin@example.com");

        if (orderRepository.findByUserId(user.getId()).isEmpty()) {
            log.info("Seeding orders");

            Product iphone = productRepository.findBySlug("iphone-16-pro-max").get();
            Product nikeShoes = productRepository.findBySlug("nike-air-max-270").get();
            Product sonyHeadphones = productRepository.findBySlug("sony-wh-1000xm6").get();

            ProductVariant iphoneVariant = productVariantRepository.findBySku("APL-IP16PM-256GB").get();
            ProductVariant nikeVariant = productVariantRepository.findBySku("NKE-AM270-US9").get();

            Coupon welcome10 = couponRepository.findByCode("WELCOME10").get();

            Order order1 = buildOrder(user, pending, usd, welcome10,
                    List.of(OrderItem.builder()
                                    .productId(iphone.getId())
                                    .variantId(iphoneVariant.getId())
                                    .productName("iPhone 16 Pro Max")
                                    .variantName("iPhone 16 Pro Max (256GB)")
                                    .sku(iphoneVariant.getSku())
                                    .quantity(1)
                                    .unitPrice(BigDecimal.valueOf(1199.99))
                                    .totalPrice(BigDecimal.valueOf(1199.99))
                                    .build(),
                            OrderItem.builder()
                                    .productId(sonyHeadphones.getId())
                                    .productName("Sony WH-1000XM6")
                                    .variantName("Sony WH-1000XM6 (Black)")
                                    .sku("SONY-WH1000XM6-Black")
                                    .quantity(1)
                                    .unitPrice(BigDecimal.valueOf(349.99))
                                    .totalPrice(BigDecimal.valueOf(349.99))
                                    .build()),
                    List.of(OrderStatusHistory.builder()
                            .toStatus(pending)
                            .changedBy("SYSTEM")
                            .reason("Order placed")
                            .build()));
            order1.setSubtotal(BigDecimal.valueOf(1549.98));
            order1.setDiscount(BigDecimal.valueOf(50.00));
            order1.setShippingCost(BigDecimal.valueOf(0));
            order1.setTax(BigDecimal.valueOf(124.00));
            order1.setTotal(BigDecimal.valueOf(1623.98));
            orderRepository.save(order1);

            Order order2 = buildOrder(admin, delivered, usd, null,
                    List.of(OrderItem.builder()
                                    .productId(nikeShoes.getId())
                                    .variantId(nikeVariant.getId())
                                    .productName("Nike Air Max 270")
                                    .variantName("Nike Air Max 270 (US 9)")
                                    .sku(nikeVariant.getSku())
                                    .quantity(2)
                                    .unitPrice(BigDecimal.valueOf(149.99))
                                    .totalPrice(BigDecimal.valueOf(299.98))
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
            order2.setSubtotal(BigDecimal.valueOf(299.98));
            order2.setDiscount(BigDecimal.ZERO);
            order2.setShippingCost(BigDecimal.valueOf(9.99));
            order2.setTax(BigDecimal.valueOf(23.99));
            order2.setTotal(BigDecimal.valueOf(333.96));
            orderRepository.save(order2);

            Order order3 = buildOrder(superAdmin, processing, usd, null,
                    List.of(OrderItem.builder()
                                    .productId(nikeShoes.getId())
                                    .productName("Nike Air Max 270")
                                    .variantName("Nike Air Max 270 (US 10)")
                                    .sku("NKE-AM270-US10")
                                    .quantity(1)
                                    .unitPrice(BigDecimal.valueOf(149.99))
                                    .totalPrice(BigDecimal.valueOf(149.99))
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
            order3.setSubtotal(BigDecimal.valueOf(149.99));
            order3.setDiscount(BigDecimal.ZERO);
            order3.setShippingCost(BigDecimal.valueOf(5.99));
            order3.setTax(BigDecimal.valueOf(11.99));
            order3.setTotal(BigDecimal.valueOf(167.97));
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
        Currency usd = getCurrency("USD");

        List<Order> orders = orderRepository.findAll();
        for (Order order : orders) {
            if (paymentRepository.findByOrderId(order.getId()).isEmpty()) {
                log.info("Seeding payment for order {}", order.getOrderNumber());
                paymentRepository.save(Payment.builder()
                        .orderId(order.getId())
                        .user(order.getUser())
                        .gateway(mock)
                        .amount(order.getTotal())
                        .currency(usd)
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

        Product iphone = productRepository.findBySlug("iphone-16-pro-max").get();
        Product galaxy = productRepository.findBySlug("samsung-galaxy-s25-ultra").get();
        Product macbook = productRepository.findBySlug("macbook-air-m4").get();
        Product sony = productRepository.findBySlug("sony-wh-1000xm6").get();
        Product nike = productRepository.findBySlug("nike-air-max-270").get();
        Product galaxyCheck = productRepository.findBySlug("samsung-galaxy-s25-ultra").get();

        List<Review> existing = reviewRepository.findByProductId(iphone.getId());
        if (existing.isEmpty()) {
            log.info("Seeding reviews");

            reviewRepository.save(Review.builder()
                    .product(iphone)
                    .user(admin)
                    .rating(5)
                    .title("Best iPhone ever!")
                    .comment("The camera is incredible and the battery life lasts me two days. Highly recommend!")
                    .isActive(true)
                    .isVerifiedPurchase(true)
                    .build());

            reviewRepository.save(Review.builder()
                    .product(iphone)
                    .user(user)
                    .rating(4)
                    .title("Great phone, but heavy")
                    .comment("Amazing performance and display. A bit on the heavier side though.")
                    .isActive(true)
                    .isVerifiedPurchase(true)
                    .build());

            reviewRepository.save(Review.builder()
                    .product(galaxy)
                    .user(admin)
                    .rating(5)
                    .title("Samsung's best")
                    .comment("The S Pen integration is flawless. Best Android phone on the market.")
                    .isActive(true)
                    .isVerifiedPurchase(true)
                    .build());

            reviewRepository.save(Review.builder()
                    .product(macbook)
                    .user(user)
                    .rating(5)
                    .title("Perfect laptop")
                    .comment("Light, powerful, and the battery lasts all day. Best laptop I've owned.")
                    .isActive(true)
                    .isVerifiedPurchase(true)
                    .build());

            reviewRepository.save(Review.builder()
                    .product(sony)
                    .user(user)
                    .rating(5)
                    .title("Noise cancellation is magic")
                    .comment("I can't hear anything with these on. Perfect for commuting and focus.")
                    .isActive(true)
                    .isVerifiedPurchase(true)
                    .build());

            reviewRepository.save(Review.builder()
                    .product(nike)
                    .user(admin)
                    .rating(4)
                    .title("Comfortable sneakers")
                    .comment("Very comfortable for daily wear. The Air unit really works.")
                    .isActive(true)
                    .isVerifiedPurchase(false)
                    .build());

            reviewRepository.save(Review.builder()
                    .product(galaxyCheck)
                    .user(user)
                    .rating(3)
                    .title("Good but overpriced")
                    .comment("Great features but the price is hard to justify over the previous model.")
                    .isActive(true)
                    .isVerifiedPurchase(false)
                    .build());
        }
    }

    private void seedCartItems() {
        User user = getUser("user@example.com");
        User admin = getUser("admin@example.com");

        Product galaxy = productRepository.findBySlug("samsung-galaxy-s25-ultra").get();
        Product macbook = productRepository.findBySlug("macbook-air-m4").get();
        Product sony = productRepository.findBySlug("sony-wh-1000xm6").get();

        if (cartRepository.findByUserId(user.getId()).isEmpty()) {
            log.info("Seeding cart items");

            cartRepository.save(CartItem.builder()
                    .user(user)
                    .product(galaxy)
                    .quantity(1)
                    .build());

            cartRepository.save(CartItem.builder()
                    .user(admin)
                    .product(macbook)
                    .quantity(1)
                    .build());

            cartRepository.save(CartItem.builder()
                    .user(admin)
                    .product(sony)
                    .quantity(2)
                    .build());
        }
    }

    private void seedWishlistItems() {
        User user = getUser("user@example.com");
        User admin = getUser("admin@example.com");

        Product macbook = productRepository.findBySlug("macbook-air-m4").get();
        Product lgTV = productRepository.findBySlug("lg-oled-evo-c5-65").get();
        Product dellXps = productRepository.findBySlug("dell-xps-16").get();

        if (!wishlistRepository.existsByUserIdAndProductId(user.getId(), macbook.getId())) {
            log.info("Seeding wishlist items");

            wishlistRepository.save(WishlistItem.builder()
                    .user(user)
                    .product(macbook)
                    .build());

            wishlistRepository.save(WishlistItem.builder()
                    .user(user)
                    .product(lgTV)
                    .build());

            wishlistRepository.save(WishlistItem.builder()
                    .user(admin)
                    .product(dellXps)
                    .build());

            wishlistRepository.save(WishlistItem.builder()
                    .user(admin)
                    .product(lgTV)
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
                    .title("Welcome to E-Commerce!")
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
                    .body("Product 'LG 65\" OLED evo C5 (77-inch)' is running low on stock.")
                    .deepLink("/admin/products/8")
                    .isRead(false)
                    .build());
        }
    }
}
