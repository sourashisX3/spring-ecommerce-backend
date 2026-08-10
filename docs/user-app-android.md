# E-Commerce — Android User App

> **Target:** Android Mobile + Tablet (End Users)  
> **Architecture:** MVVM + Clean Architecture  
> **Tech:** Jetpack Compose, Hilt, Room, Retrofit, Kotlin Serialization, Coil  
> **Base URL:** `http://host:8083/api/v1`

---

## Project Structure (PookieMon Pattern)

```
app/src/main/java/com/ecommerce/user/
├── core/
│   ├── config/
│   │   ├── navigation/
│   │   │   ├── AppNavGraph.kt        # NavHost setup
│   │   │   ├── Route.kt              # All route sealed class
│   │   │   └── BottomNavItem.kt      # Bottom nav tabs
│   │   └── network/
│   │       ├── ApiConstants.kt       # Endpoint paths
│   │       └── EnvironmentConfig.kt  # Base URL per flavor
│   ├── database/
│   │   ├── AppDatabase.kt
│   │   └── Converters.kt
│   ├── di/
│   │   ├── AppModule.kt             # Hilt: app-scoped
│   │   ├── NetworkModule.kt         # Hilt: Retrofit, OkHttp
│   │   ├── DatabaseModule.kt        # Hilt: Room
│   │   └── RepositoryModule.kt      # Hilt: repos
│   ├── network/
│   │   ├── AuthInterceptor.kt        # Bearer token + refresh
│   │   └── ApiResponse.kt           # Generic API wrapper
│   ├── theme/
│   │   ├── Theme.kt
│   │   ├── Color.kt
│   │   ├── Type.kt
│   │   └── Shape.kt
│   ├── ui/
│   │   └── components/              # Shared composables
│   │       ├── ProductCard.kt
│   │       ├── LoadingIndicator.kt
│   │       ├── ErrorView.kt
│   │       ├── EmptyState.kt
│   │       ├── SearchBar.kt
│   │       ├── PriceText.kt
│   │       ├── RatingBar.kt
│   │       └── QuantitySelector.kt
│   └── utils/
│       ├── Constants.kt
│       ├── Extensions.kt
│       ├── DateUtils.kt
│       └── ValidationUtils.kt
└── feature/
    ├── auth/
    │   ├── data/
    │   │   ├── datasource/remote/
    │   │   │   └── AuthApi.kt         # Retrofit interface
    │   │   ├── datasource/local/
    │   │   │   └── AuthLocalDataSource.kt  # Token storage (DataStore)
    │   │   ├── dto/
    │   │   │   ├── LoginRequest.kt
    │   │   │   ├── RegisterRequest.kt
    │   │   │   ├── SendOtpRequest.kt
    │   │   │   ├── VerifyOtpRequest.kt
    │   │   │   ├── RefreshTokenRequest.kt
    │   │   │   └── AuthResponse.kt
    │   │   ├── mapper/
    │   │   │   └── AuthMapper.kt
    │   │   └── repository/
    │   │       └── AuthRepositoryImpl.kt
    │   ├── domain/
    │   │   ├── model/
    │   │   │   └── User.kt
    │   │   ├── repository/
    │   │   │   └── AuthRepository.kt
    │   │   └── usecase/
    │   │       ├── LoginUseCase.kt
    │   │       ├── RegisterUseCase.kt
    │   │       ├── RefreshTokenUseCase.kt
    │   │       ├── SendOtpUseCase.kt
    │   │       ├── VerifyOtpUseCase.kt
    │   │       └── LogoutUseCase.kt
    │   └── presentation/
    │       ├── components/
    │       │   ├── AuthTextField.kt
    │       │   └── SocialLoginButton.kt
    │       ├── di/
    │       │   └── AuthModule.kt
    │       ├── events/
    │       │   └── AuthEvent.kt
    │       ├── screens/
    │       │   ├── LoginScreen.kt
    │       │   ├── RegisterScreen.kt
    │       │   ├── OtpVerificationScreen.kt
    │       │   └── ForgotPasswordScreen.kt
    │       ├── states/
    │       │   ├── LoginState.kt
    │       │   ├── RegisterState.kt
    │       │   └── OtpState.kt
    │       └── viewmodels/
    │           ├── LoginViewModel.kt
    │           ├── RegisterViewModel.kt
    │           └── OtpViewModel.kt
    ├── home/
    │   ├── data/
    │   │   ├── datasource/remote/
    │   │   │   └── HomeApi.kt
    │   │   ├── dto/
    │   │   │   └── HomeDataDto.kt
    │   │   ├── mapper/
    │   │   │   └── HomeMapper.kt
    │   │   └── repository/
    │   │       └── HomeRepositoryImpl.kt
    │   ├── domain/
    │   │   ├── model/
    │   │   │   └── HomeData.kt
    │   │   ├── repository/
    │   │   │   └── HomeRepository.kt
    │   │   └── usecase/
    │   │       └── GetHomeDataUseCase.kt
    │   └── presentation/
    │       ├── components/
    │       │   ├── FeaturedProductCarousel.kt
    │       │   ├── CategoryGrid.kt
    │       │   └── BrandRow.kt
    │       ├── events/
    │       │   └── HomeEvent.kt
    │       ├── screens/
    │       │   └── HomeScreen.kt
    │       ├── states/
    │       │   └── HomeState.kt
    │       └── viewmodels/
    │           └── HomeViewModel.kt
    ├── product/
    │   ├── data/
    │   │   ├── dao/
    │   │   │   └── ProductCacheDao.kt       # Room DAO
    │   │   ├── datasource/
    │   │   │   ├── local/
    │   │   │   │   └── ProductLocalDataSource.kt
    │   │   │   └── remote/
    │   │   │       └── ProductApi.kt
    │   │   ├── dto/
    │   │   │   ├── ProductResponse.kt
    │   │   │   ├── ProductRequest.kt
    │   │   │   └── ReviewResponse.kt
    │   │   ├── entity/
    │   │   │   └── ProductCacheEntity.kt    # Room entity
    │   │   ├── mapper/
    │   │   │   └── ProductMapper.kt
    │   │   └── repository/
    │   │       └── ProductRepositoryImpl.kt
    │   ├── domain/
    │   │   ├── model/
    │   │   │   ├── Product.kt
    │   │   │   ├── ProductVariant.kt
    │   │   │   ├── ProductImage.kt
    │   │   │   └── Review.kt
    │   │   ├── repository/
    │   │   │   └── ProductRepository.kt
    │   │   └── usecase/
    │   │       ├── GetProductListUseCase.kt
    │   │       ├── GetProductDetailUseCase.kt
    │   │       ├── GetSimilarProductsUseCase.kt
    │   │       └── SearchProductsUseCase.kt
    │   └── presentation/
    │       ├── components/
    │       │   ├── ProductListItem.kt
    │       │   ├── ProductImageCarousel.kt
    │       │   ├── VariantSelector.kt
    │       │   ├── ReviewCard.kt
    │       │   ├── RatingSummary.kt
    │       │   └── ProductFilterSheet.kt
    │       ├── di/
    │       │   └── ProductModule.kt
    │       ├── events/
    │       │   ├── ProductListEvent.kt
    │       │   └── ProductDetailEvent.kt
    │       ├── screens/
    │       │   ├── ProductListScreen.kt
    │       │   ├── ProductDetailScreen.kt
    │       │   └── ProductSearchScreen.kt
    │       ├── states/
    │       │   ├── ProductListState.kt
    │       │   └── ProductDetailState.kt
    │       └── viewmodels/
    │           ├── ProductListViewModel.kt
    │           └── ProductDetailViewModel.kt
    ├── cart/
    │   ├── data/
    │   │   ├── datasource/remote/
    │   │   │   └── CartApi.kt
    │   │   ├── dto/
    │   │   │   ├── CartItemResponse.kt
    │   │   │   └── CartItemRequest.kt
    │   │   ├── mapper/
    │   │   │   └── CartMapper.kt
    │   │   └── repository/
    │   │       └── CartRepositoryImpl.kt
    │   ├── domain/
    │   │   ├── model/
    │   │   │   └── CartItem.kt
    │   │   ├── repository/
    │   │   │   └── CartRepository.kt
    │   │   └── usecase/
    │   │       ├── GetCartUseCase.kt
    │   │       ├── AddToCartUseCase.kt
    │   │       ├── UpdateCartItemUseCase.kt
    │   │       ├── RemoveFromCartUseCase.kt
    │   │       └── ClearCartUseCase.kt
    │   └── presentation/
    │       ├── components/
    │       │   ├── CartItemCard.kt
    │       │   └── CartSummary.kt
    │       ├── di/
    │       │   └── CartModule.kt
    │       ├── events/
    │       │   └── CartEvent.kt
    │       ├── screens/
    │       │   └── CartScreen.kt
    │       ├── states/
    │       │   └── CartState.kt
    │       └── viewmodels/
    │           └── CartViewModel.kt
    ├── checkout/
    │   └── presentation/
    │       ├── components/
    │       │   ├── AddressSelector.kt
    │       │   ├── PaymentMethodSelector.kt
    │       │   ├── CouponInput.kt
    │       │   └── OrderSummaryCard.kt
    │       ├── events/
    │       │   └── CheckoutEvent.kt
    │       ├── screens/
    │       │   └── CheckoutScreen.kt
    │       ├── states/
    │       │   └── CheckoutState.kt
    │       └── viewmodels/
    │           └── CheckoutViewModel.kt
    ├── order/
    │   ├── data/
    │   │   ├── datasource/remote/
    │   │   │   └── OrderApi.kt
    │   │   ├── dto/
    │   │   │   ├── OrderResponse.kt
    │   │   │   └── OrderRequest.kt
    │   │   ├── mapper/
    │   │   │   └── OrderMapper.kt
    │   │   └── repository/
    │   │       └── OrderRepositoryImpl.kt
    │   ├── domain/
    │   │   ├── model/
    │   │   │   └── Order.kt
    │   │   ├── repository/
    │   │   │   └── OrderRepository.kt
    │   │   └── usecase/
    │   │       ├── CheckoutUseCase.kt
    │   │       ├── GetUserOrdersUseCase.kt
    │   │       ├── GetOrderDetailUseCase.kt
    │   │       └── CancelOrderUseCase.kt
    │   └── presentation/
    │       ├── components/
    │       │   ├── OrderCard.kt
    │       │   ├── OrderStatusTimeline.kt
    │       │   └── OrderItemRow.kt
    │       ├── di/
    │       │   └── OrderModule.kt
    │       ├── events/
    │       │   ├── OrderListEvent.kt
    │       │   └── OrderDetailEvent.kt
    │       ├── screens/
    │       │   ├── OrderListScreen.kt
    │       │   └── OrderDetailScreen.kt
    │       ├── states/
    │       │   ├── OrderListState.kt
    │       │   └── OrderDetailState.kt
    │       └── viewmodels/
    │           ├── OrderListViewModel.kt
    │           └── OrderDetailViewModel.kt
    ├── wishlist/
    │   ├── data/
    │   │   └── ... (same pattern)
    │   └── presentation/
    │       ├── screens/
    │       │   └── WishlistScreen.kt
    │       └── viewmodels/
    │           └── WishlistViewModel.kt
    ├── review/
    │   ├── data/
    │   │   └── ...
    │   └── presentation/
    │       ├── screens/
    │       │   └── WriteReviewScreen.kt
    │       └── viewmodels/
    │           └── ReviewViewModel.kt
    ├── address/
    │   ├── data/
    │   │   └── ...
    │   └── presentation/
    │       ├── screens/
    │       │   ├── AddressListScreen.kt
    │       │   └── AddressFormScreen.kt
    │       └── viewmodels/
    │           └── AddressViewModel.kt
    ├── wallet/
    │   ├── data/
    │   │   └── ...
    │   └── presentation/
    │       ├── screens/
    │       │   └── WalletScreen.kt
    │       └── viewmodels/
    │           └── WalletViewModel.kt
    ├── profile/
    │   ├── data/
    │   │   └── ...
    │   └── presentation/
    │       ├── screens/
    │       │   ├── ProfileScreen.kt
    │       │   └── EditProfileScreen.kt
    │       └── viewmodels/
    │           └── ProfileViewModel.kt
    ├── notification/
    │   ├── data/
    │   │   └── ...
    │   └── presentation/
    │       ├── screens/
    │       │   └── NotificationScreen.kt
    │       └── viewmodels/
    │           └── NotificationViewModel.kt
    └── chat/
        ├── data/
        │   └── ...
        └── presentation/
            ├── screens/
            │   ├── ChatListScreen.kt
            │   └── ChatDetailScreen.kt
            └── viewmodels/
                └── ChatViewModel.kt
```

---

## Dependencies (`build.gradle.kts`)

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

dependencies {
    // ── Compose BOM ─────────────────────────
    val composeBom = platform("androidx.compose:compose-bom:2026.07.00")
    implementation(composeBom)

    // ── Core ────────────────────────────────
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.11.0")
    implementation("androidx.activity:activity-compose:1.10.0")

    // ── Compose ─────────────────────────────
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.runtime:runtime")

    // ── Navigation ──────────────────────────
    implementation("androidx.navigation:navigation-compose:2.9.0")

    // ── Lifecycle / ViewModel ────────────────
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.0")

    // ── Hilt ────────────────────────────────
    implementation("com.google.dagger:hilt-android:2.54")
    ksp("com.google.dagger:hilt-compiler:2.54")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // ── Retrofit + OkHttp ────────────────────
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ── Kotlin Serialization (JSON) ──────────
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")

    // ── Room ────────────────────────────────
    implementation("androidx.room:room-runtime:2.7.0")
    implementation("androidx.room:room-ktx:2.7.0")
    ksp("androidx.room:room-compiler:2.7.0")

    // ── DataStore ────────────────────────────
    implementation("androidx.datastore:datastore-preferences:1.1.4")

    // ── Coil (images) ────────────────────────
    implementation("io.coil-kt:coil-compose:2.7.0")

    // ── Kotlinx Coroutines ───────────────────
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // ── Accompanist (pull-to-refresh, etc.) ──
    implementation("com.google.accompanist:accompanist-swiperefresh:0.36.0")
    implementation("com.google.accompanist:accompanist-placeholder:0.36.0")

    // ── Lottie (animations) ─────────────────
    implementation("com.airbnb.android:lottie-compose:6.6.0")

    // ── Testing ─────────────────────────────
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testImplementation("io.mockk:mockk:1.13.13")
    testImplementation("app.cash.turbine:turbine:1.2.0")
    testImplementation("com.google.dagger:hilt-android-testing:2.54")
    kspTest("com.google.dagger:hilt-compiler:2.54")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.navigation:navigation-testing:2.9.0")
}
```

---

## MVVM Data Flow

```
User Action → UI Event → ViewModel → UseCase → Repository → API/DB
                  ↑                                      │
                  └─────── StateFlow ─────────────────────┘
```

**Screen template:**

```kotlin
// ── states/ProductListState.kt ──
data class ProductListState(
    val isLoading: Boolean = true,
    val products: List<Product> = emptyList(),
    val error: String? = null,
    val currentPage: Int = 0,
    val hasMore: Boolean = true,
    val searchQuery: String? = null,
    val selectedCategorySlug: String? = null
)

// ── events/ProductListEvent.kt ──
sealed interface ProductListEvent {
    data object LoadInitial : ProductListEvent
    data class LoadMore(val page: Int) : ProductListEvent
    data class Search(val query: String) : ProductListEvent
    data class FilterByCategory(val slug: String?) : ProductListEvent
    data object Refresh : ProductListEvent
    data class NavigateToDetail(val productUuid: String) : ProductListEvent
}

// ── viewmodels/ProductListViewModel.kt ──
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductListUseCase: GetProductListUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProductListState())
    val state: StateFlow<ProductListState> = _state.asStateFlow()

    init { onEvent(ProductListEvent.LoadInitial) }

    fun onEvent(event: ProductListEvent) {
        when (event) {
            ProductListEvent.LoadInitial -> loadProducts(page = 0, refresh = true)
            is ProductListEvent.LoadMore -> loadProducts(page = event.page)
            is ProductListEvent.Search -> searchProducts(event.query)
            is ProductListEvent.FilterByCategory -> filterByCategory(event.slug)
            ProductListEvent.Refresh -> loadProducts(page = 0, refresh = true)
            is ProductListEvent.NavigateToDetail -> { /* handled by navigation */ }
        }
    }

    private fun loadProducts(page: Int, refresh: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getProductListUseCase(
                page = page,
                query = _state.value.searchQuery,
                categorySlug = _state.value.selectedCategorySlug
            ).onSuccess { result ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        products = if (refresh) result.items else it.products + result.items,
                        currentPage = page,
                        hasMore = result.hasMore
                    )
                }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}

// ── screens/ProductListScreen.kt ──
@Composable
fun ProductListScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: ProductListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        sheetContent = { FilterSheet(/*...*/) },
        scaffoldState = scaffoldState
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            verticalArrangement = spacedBy(12.dp)
        ) {
            items(state.products) { product ->
                ProductCard(
                    product = product,
                    onCardClick = {
                        viewModel.onEvent(ProductListEvent.NavigateToDetail(product.uuid))
                        onNavigateToDetail(product.uuid)
                    }
                )
            }
            if (state.isLoading) {
                item { LoadingIndicator() }
            }
        }
    }
}
```

---

## Navigation Structure

```
NavGraph
├── AuthGraph (startDestination = "login")
│   ├── Login
│   ├── Register
│   ├── OtpVerification
│   └── ForgotPassword
└── MainGraph (startDestination = "home")
    ├── BottomNavTabs
    │   ├── Home
    │   ├── Categories → CategoryDetail
    │   ├── Cart → Checkout
    │   ├── Orders → OrderDetail
    │   └── Profile
    ├── ProductDetail(uuid)
    ├── ProductSearch
    ├── Wishlist
    ├── WriteReview(productUuid)
    ├── AddressList → AddressForm
    ├── Wallet
    ├── Notifications
    ├── ChatList → ChatDetail
    └── EditProfile
```

---

## API Endpoints Used (User App)

| Feature | Endpoints |
|---|---|
| **Auth** | `POST /auth/register`, `POST /auth/login`, `POST /auth/refresh`, `POST /auth/send-otp`, `POST /auth/verify-otp`, `POST /auth/logout` |
| **Home** | `GET /home` |
| **Products** | `GET /products` (paginated, filtered), `GET /products/{uuid}`, `GET /products/{uuid}/similar` |
| **Categories** | `GET /categories`, `GET /categories/tree`, `GET /categories/{slug}` |
| **Brands** | `GET /brands` |
| **Cart** | `GET /carts`, `POST /carts/{productUuid}`, `PATCH /carts/{itemUuid}`, `DELETE /carts/{itemUuid}`, `DELETE /carts` |
| **Orders** | `POST /orders/checkout`, `GET /orders`, `GET /orders/{uuid}`, `PATCH /orders/{uuid}/cancel` |
| **Wishlist** | `GET /wishlist`, `POST /wishlist/{productUuid}`, `DELETE /wishlist/{itemUuid}` |
| **Reviews** | `GET /products/{uuid}/reviews`, `POST /reviews`, `POST /reviews/{id}/vote` |
| **Addresses** | `GET /addresses`, `POST /addresses`, `PUT /addresses/{uuid}`, `DELETE /addresses/{uuid}` |
| **Wallet** | `GET /wallets/me`, `GET /wallets/me/transactions` |
| **Profile** | `GET /users/me`, `PUT /users/me`, `PUT /users/me/password` |
| **Notifications** | `GET /notifications`, `GET /notifications/unread-count`, `PUT /notifications/{uuid}/read`, `PUT /notifications/read-all` |
| **Chat** | `GET /chat/rooms`, `POST /chat/rooms`, `GET /chat/rooms/{uuid}/messages`, `POST /chat/rooms/{uuid}/messages` |

---

## Environment Configuration

```kotlin
// build.gradle.kts (app level)
android {
    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8083/api/v1\"")
            buildConfigField("String", "WS_URL", "\"ws://10.0.2.2:8083/api/v1/ws\"")
        }
        create("staging") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"https://staging-api.example.com/api/v1\"")
            buildConfigField("String", "WS_URL", "\"wss://staging-api.example.com/api/v1/ws\"")
        }
        create("production") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"https://api.example.com/api/v1\"")
            buildConfigField("String", "WS_URL", "\"wss://api.example.com/api/v1/ws\"")
        }
    }
}
```

---

## Auth Flow

```
Login/Register → AuthResponse(token, refreshToken, user)
     │
     ├── Store token + refreshToken in DataStore
     ├── Attach "Authorization: Bearer <token>" via OkHttp interceptor
     │
     └── On 401 → Interceptor calls POST /auth/refresh
         ├── Success → update tokens, retry request
         └── Failure → clear DataStore, navigate to Login
```

---

## Key Design Decisions

| Decision | Choice | Rationale |
|---|---|---|
| **DI** | Hilt | Android-native, compile-time verified, Google-recommended |
| **Networking** | Retrofit + OkHttp | Type-safe, interceptor chain, Kotlin Serialization converter |
| **Database** | Room + DataStore | Room for structured cache, DataStore for preferences/tokens |
| **Images** | Coil | Compose-native, memory-aware, disk caching |
| **Navigation** | Navigation Compose | Type-safe routes, deep linking, bottom nav support |
| **State** | StateFlow + collectAsStateWithLifecycle | Lifecycle-aware, no leaks, Compose-native |
| **Architecture** | MVVM + Clean Architecture | Separated concerns, testable, PookieMon pattern |
| **Async** | viewModelScope + coroutines | Structured concurrency, lifecycle-aware |
