# E-Commerce — Android Admin App

> **Target:** Android Mobile + Tablet (Store Admins)  
> **Architecture:** MVVM + Clean Architecture  
> **Tech:** Jetpack Compose, Hilt, Room, Retrofit, Kotlin Serialization, Coil  
> **Base URL:** `http://host:8083/api/v1`

---

## Project Structure

```
app/src/main/java/com/ecommerce/admin/
├── core/
│   ├── config/
│   │   ├── navigation/
│   │   │   ├── AppNavGraph.kt
│   │   │   └── Route.kt
│   │   └── network/
│   │       ├── ApiConstants.kt
│   │       └── EnvironmentConfig.kt
│   ├── database/
│   │   └── AppDatabase.kt
│   ├── di/
│   │   ├── AppModule.kt
│   │   ├── NetworkModule.kt
│   │   ├── DatabaseModule.kt
│   │   └── RepositoryModule.kt
│   ├── network/
│   │   ├── AuthInterceptor.kt
│   │   ├── ApiResponse.kt
│   │   └── Pagination.kt
│   ├── theme/
│   │   ├── Theme.kt
│   │   ├── Color.kt
│   │   ├── Type.kt
│   │   └── Shape.kt
│   ├── ui/
│   │   └── components/
│   │       ├── AdminTopBar.kt
│   │       ├── StatCard.kt
│   │       ├── DataTable.kt
│   │       ├── StatusChip.kt
│   │       ├── ConfirmDialog.kt
│   │       ├── EmptyState.kt
│   │       ├── LoadingIndicator.kt
│   │       └── SearchBar.kt
│   └── utils/
│       ├── Constants.kt
│       ├── Extensions.kt
│       ├── DateUtils.kt
│       └── CurrencyUtils.kt
└── feature/
    ├── auth/
    │   ├── data/
    │   │   ├── datasource/remote/
    │   │   │   └── AuthApi.kt
    │   │   ├── datasource/local/
    │   │   │   └── TokenStorage.kt
    │   │   ├── dto/
    │   │   │   └── AdminLoginRequest.kt
    │   │   ├── mapper/
    │   │   │   └── AuthMapper.kt
    │   │   └── repository/
    │   │       └── AuthRepositoryImpl.kt
    │   ├── domain/
    │   │   ├── model/
    │   │   │   └── AdminUser.kt
    │   │   ├── repository/
    │   │   │   └── AuthRepository.kt
    │   │   └── usecase/
    │   │       ├── AdminLoginUseCase.kt
    │   │       └── AdminLogoutUseCase.kt
    │   └── presentation/
    │       ├── components/
    │       │   └── AdminLoginTextField.kt
    │       ├── events/
    │       │   └── AdminLoginEvent.kt
    │       ├── screens/
    │       │   └── AdminLoginScreen.kt
    │       ├── states/
    │       │   └── AdminLoginState.kt
    │       └── viewmodels/
    │           └── AdminLoginViewModel.kt
    ├── dashboard/
    │   ├── data/
    │   │   ├── datasource/remote/
    │   │   │   └── DashboardApi.kt
    │   │   ├── dto/
    │   │   │   └── DashboardDto.kt
    │   │   ├── mapper/
    │   │   │   └── DashboardMapper.kt
    │   │   └── repository/
    │   │       └── DashboardRepositoryImpl.kt
    │   ├── domain/
    │   │   ├── model/
    │   │   │   └── DashboardData.kt
    │   │   ├── repository/
    │   │   │   └── DashboardRepository.kt
    │   │   └── usecase/
    │   │       └── GetDashboardUseCase.kt
    │   └── presentation/
    │       ├── components/
    │       │   ├── RevenueChart.kt
    │       │   ├── OrderStatsRow.kt
    │       │   └── RecentOrdersList.kt
    │       ├── events/
    │       │   └── DashboardEvent.kt
    │       ├── screens/
    │       │   └── DashboardScreen.kt
    │       ├── states/
    │       │   └── DashboardState.kt
    │       └── viewmodels/
    │           └── DashboardViewModel.kt
    ├── products/
    │   ├── data/
    │   │   ├── datasource/remote/
    │   │   │   └── ProductApi.kt
    │   │   ├── dto/
    │   │   │   ├── ProductResponse.kt
    │   │   │   ├── ProductRequest.kt
    │   │   │   └── VariantRequest.kt
    │   │   ├── mapper/
    │   │   │   └── ProductMapper.kt
    │   │   └── repository/
    │   │       └── ProductRepositoryImpl.kt
    │   ├── domain/
    │   │   ├── model/
    │   │   │   ├── AdminProduct.kt
    │   │   │   └── ProductVariant.kt
    │   │   ├── repository/
    │   │   │   └── ProductRepository.kt
    │   │   └── usecase/
    │   │       ├── GetProductsUseCase.kt
    │   │       ├── GetProductDetailUseCase.kt
    │   │       ├── CreateProductUseCase.kt
    │   │       ├── UpdateProductUseCase.kt
    │   │       ├── ToggleProductStatusUseCase.kt
    │   │       ├── DeleteProductUseCase.kt
    │   │       ├── CreateVariantUseCase.kt
    │   │       ├── UpdateVariantUseCase.kt
    │   │       ├── UpdateStockUseCase.kt
    │   │       └── DeleteVariantUseCase.kt
    │   └── presentation/
    │       ├── components/
    │       │   ├── ProductFormFields.kt
    │       │   ├── VariantEditor.kt
    │       │   ├── ImagePicker.kt
    │       │   ├── AttributeEditor.kt
    │       │   └── ProductTable.kt
    │       ├── di/
    │       │   └── ProductModule.kt
    │       ├── events/
    │       │   ├── ProductListEvent.kt
    │       │   └── ProductFormEvent.kt
    │       ├── screens/
    │       │   ├── ProductListScreen.kt
    │       │   ├── ProductDetailScreen.kt
    │       │   ├── ProductCreateScreen.kt
    │       │   └── ProductEditScreen.kt
    │       ├── states/
    │       │   ├── ProductListState.kt
    │       │   └── ProductFormState.kt
    │       └── viewmodels/
    │           ├── ProductListViewModel.kt
    │           └── ProductFormViewModel.kt
    ├── categories/
    │   ├── data/
    │   │   ├── datasource/remote/
    │   │   │   └── CategoryApi.kt
    │   │   ├── dto/
    │   │   │   ├── CategoryResponse.kt
    │   │   │   └── CategoryRequest.kt
    │   │   ├── mapper/
    │   │   │   └── CategoryMapper.kt
    │   │   └── repository/
    │   │       └── CategoryRepositoryImpl.kt
    │   ├── domain/
    │   │   ├── model/
    │   │   │   └── AdminCategory.kt
    │   │   ├── repository/
    │   │   │   └── CategoryRepository.kt
    │   │   └── usecase/
    │   │       ├── GetCategoryTreeUseCase.kt
    │   │       ├── CreateCategoryUseCase.kt
    │   │       ├── UpdateCategoryUseCase.kt
    │   │       ├── ToggleCategoryStatusUseCase.kt
    │   │       └── DeleteCategoryUseCase.kt
    │   └── presentation/
    │       ├── components/
    │       │   ├── CategoryTreeItem.kt
    │       │   └── CategoryFormDialog.kt
    │       ├── events/
    │       │   └── CategoryEvent.kt
    │       ├── screens/
    │       │   └── CategoryManagementScreen.kt
    │       ├── states/
    │       │   └── CategoryState.kt
    │       └── viewmodels/
    │           └── CategoryViewModel.kt
    ├── brands/
    │   ├── data/
    │   │   └── ... (same pattern as categories)
    │   └── presentation/
    │       ├── screens/
    │       │   └── BrandManagementScreen.kt
    │       └── viewmodels/
    │           └── BrandViewModel.kt
    ├── tags/
    │   ├── data/
    │   │   └── ...
    │   └── presentation/
    │       ├── screens/
    │       │   └── TagManagementScreen.kt
    │       └── viewmodels/
    │           └── TagViewModel.kt
    ├── orders/
    │   ├── data/
    │   │   ├── datasource/remote/
    │   │   │   └── OrderApi.kt
    │   │   ├── dto/
    │   │   │   ├── OrderResponse.kt
    │   │   │   └── UpdateOrderStatusRequest.kt
    │   │   ├── mapper/
    │   │   │   └── OrderMapper.kt
    │   │   └── repository/
    │   │       └── OrderRepositoryImpl.kt
    │   ├── domain/
    │   │   ├── model/
    │   │   │   └── AdminOrder.kt
    │   │   ├── repository/
    │   │   │   └── OrderRepository.kt
    │   │   └── usecase/
    │   │       ├── GetAllOrdersUseCase.kt
    │   │       ├── GetOrderDetailUseCase.kt
    │   │       └── UpdateOrderStatusUseCase.kt
    │   └── presentation/
    │       ├── components/
    │       │   ├── OrderTable.kt
    │       │   ├── OrderStatusDropdown.kt
    │       │   ├── OrderItemRow.kt
    │       │   └── OrderDetailSheet.kt
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
    ├── users/
    │   ├── data/
    │   │   ├── datasource/remote/
    │   │   │   └── AdminUserApi.kt
    │   │   ├── dto/
    │   │   │   └── UserResponse.kt
    │   │   ├── mapper/
    │   │   │   └── UserMapper.kt
    │   │   └── repository/
    │   │       └── UserRepositoryImpl.kt
    │   ├── domain/
    │   │   ├── model/
    │   │   │   └── AdminUserInfo.kt
    │   │   ├── repository/
    │   │   │   └── UserRepository.kt
    │   │   └── usecase/
    │   │       ├── GetUserListUseCase.kt
    │   │       ├── GetUserDetailUseCase.kt
    │   │       └── ToggleUserStatusUseCase.kt
    │   └── presentation/
    │       ├── components/
    │       │   ├── UserTable.kt
    │       │   └── UserDetailSheet.kt
    │       ├── events/
    │       │   └── UserListEvent.kt
    │       ├── screens/
    │       │   ├── UserListScreen.kt
    │       │   └── UserDetailScreen.kt
    │       ├── states/
    │       │   └── UserListState.kt
    │       └── viewmodels/
    │           └── UserListViewModel.kt
    ├── coupons/
    │   ├── data/
    │   │   ├── datasource/remote/
    │   │   │   └── CouponApi.kt
    │   │   ├── dto/
    │   │   │   ├── CouponResponse.kt
    │   │   │   └── CouponRequest.kt
    │   │   ├── mapper/
    │   │   │   └── CouponMapper.kt
    │   │   └── repository/
    │   │       └── CouponRepositoryImpl.kt
    │   ├── domain/
    │   │   ├── model/
    │   │   │   └── AdminCoupon.kt
    │   │   ├── repository/
    │   │   │   └── CouponRepository.kt
    │   │   └── usecase/
    │   │       ├── GetCouponsUseCase.kt
    │   │       ├── CreateCouponUseCase.kt
    │   │       ├── UpdateCouponUseCase.kt
    │   │       └── DeleteCouponUseCase.kt
    │   └── presentation/
    │       ├── components/
    │       │   ├── CouponTable.kt
    │       │   └── CouponForm.kt
    │       ├── events/
    │       │   └── CouponEvent.kt
    │       ├── screens/
    │       │   └── CouponManagementScreen.kt
    │       ├── states/
    │       │   └── CouponState.kt
    │       └── viewmodels/
    │           └── CouponViewModel.kt
    ├── discounts/
    │   └── ... (mirrors coupons pattern)
    ├── offers/
    │   └── ... (mirrors coupons pattern)
    ├── payments/
    │   ├── data/
    │   │   ├── datasource/remote/
    │   │   │   └── PaymentApi.kt
    │   │   ├── dto/
    │   │   │   └── PaymentResponse.kt
    │   │   ├── mapper/
    │   │   │   └── PaymentMapper.kt
    │   │   └── repository/
    │   │       └── PaymentRepositoryImpl.kt
    │   ├── domain/
    │   │   ├── model/
    │   │   │   └── AdminPayment.kt
    │   │   ├── repository/
    │   │   │   └── PaymentRepository.kt
    │   │   └── usecase/
    │   │       ├── GetPaymentsUseCase.kt
    │   │       └── ProcessRefundUseCase.kt
    │   └── presentation/
    │       ├── components/
    │       │   ├── PaymentTable.kt
    │       │   └── RefundDialog.kt
    │       ├── events/
    │       │   └── PaymentEvent.kt
    │       ├── screens/
    │       │   └── PaymentManagementScreen.kt
    │       ├── states/
    │       │   └── PaymentState.kt
    │       └── viewmodels/
    │           └── PaymentViewModel.kt
    ├── shipping/
    │   ├── data/
    │   │   ├── datasource/remote/
    │   │   │   ├── DeliveryApi.kt
    │   │   │   └── CarrierApi.kt
    │   │   ├── dto/
    │   │   │   └── DeliveryResponse.kt
    │   │   ├── mapper/
    │   │   │   └── DeliveryMapper.kt
    │   │   └── repository/
    │   │       └── DeliveryRepositoryImpl.kt
    │   ├── domain/
    │   │   ├── model/
    │   │   │   └── AdminDelivery.kt
    │   │   ├── repository/
    │   │   │   └── DeliveryRepository.kt
    │   │   └── usecase/
    │   │       ├── GetDeliveriesUseCase.kt
    │   │       ├── UpdateDeliveryStatusUseCase.kt
    │   │       └── GetCarriersUseCase.kt
    │   └── presentation/
    │       ├── components/
    │       │   └── DeliveryTable.kt
    │       ├── events/
    │       │   └── DeliveryEvent.kt
    │       ├── screens/
    │       │   └── DeliveryManagementScreen.kt
    │       ├── states/
    │       │   └── DeliveryState.kt
    │       └── viewmodels/
    │           └── DeliveryViewModel.kt
    ├── returns/
    │   ├── data/
    │   │   ├── datasource/remote/
    │   │   │   └── ReturnApi.kt
    │   │   ├── dto/
    │   │   │   └── ReturnRequestResponse.kt
    │   │   ├── mapper/
    │   │   │   └── ReturnMapper.kt
    │   │   └── repository/
    │   │       └── ReturnRepositoryImpl.kt
    │   ├── domain/
    │   │   ├── model/
    │   │   │   └── AdminReturnRequest.kt
    │   │   ├── repository/
    │   │   │   └── ReturnRepository.kt
    │   │   └── usecase/
    │   │       ├── GetAllReturnsUseCase.kt
    │   │       └── UpdateReturnStatusUseCase.kt
    │   └── presentation/
    │       ├── components/
    │       │   ├── ReturnRequestCard.kt
    │       │   └── ReturnStatusDialog.kt
    │       ├── events/
    │       │   └── ReturnEvent.kt
    │       ├── screens/
    │       │   └── ReturnManagementScreen.kt
    │       ├── states/
    │       │   └── ReturnState.kt
    │       └── viewmodels/
    │           └── ReturnViewModel.kt
    └── notifications/
        ├── data/
        │   └── ...
        └── presentation/
            ├── screens/
            │   └── NotificationListScreen.kt
            └── viewmodels/
                └── NotificationViewModel.kt
```

---

## Dependencies (Same as User App)

```kotlin
// Same base dependencies as user-app-android.md
// Additional admin-specific:
dependencies {
    // Charts / analytics
    implementation("com.patrykandpatrick.vico:compose-m3:2.1.0")

    // PDF / export
    implementation("com.itextpdf:itext7-core:8.0.5")
}
```

---

## Navigation Structure (Admin)

```
AdminNavGraph
├── AuthGraph
│   └── AdminLogin
└── MainGraph
    ├── Dashboard (bottom nav)
    ├── Products (bottom nav)
    │   ├── ProductList
    │   ├── ProductDetail/{uuid}
    │   ├── ProductCreate
    │   └── ProductEdit/{uuid}
    ├── Orders (bottom nav)
    │   ├── OrderList
    │   └── OrderDetail/{uuid}
    ├── More (bottom nav)
    │   ├── Categories
    │   ├── Brands
    │   ├── Tags
    │   ├── Users
    │   ├── Coupons
    │   ├── Discounts
    │   ├── Offers
    │   ├── Payments
    │   ├── Shipping / Deliveries
    │   ├── Returns
    │   └── Notifications
    └── Profile
```

---

## API Endpoints Used (Admin App)

| Feature | Endpoints | Permissions |
|---|---|---|
| **Auth** | `POST /auth/login`, `POST /auth/refresh`, `POST /auth/logout` | — |
| **Dashboard** | `GET /home/dashboard` | authenticated |
| **Products** | `GET /products`, `GET /products/{uuid}`, `POST /products`, `PUT /products/{uuid}`, `PATCH /products/{uuid}/status`, `DELETE /products/{uuid}`, `POST /variants`, `PUT /variants/{uuid}`, `DELETE /variants/{uuid}` | `product:*` |
| **Categories** | `GET /categories/tree`, `POST /categories`, `PUT /categories/{uuid}`, `PATCH /categories/{uuid}/status`, `DELETE /categories/{uuid}` | `category:*` |
| **Brands** | `GET /brands`, `POST /brands`, `PUT /brands/{uuid}`, `PATCH /brands/{uuid}/status`, `DELETE /brands/{uuid}` | `brand:*` |
| **Tags** | `GET /tags`, `POST /tags`, `PUT /tags/{uuid}`, `PATCH /tags/{uuid}/status`, `DELETE /tags/{uuid}` | `tag:*` |
| **Orders** | `GET /orders`, `GET /orders/{uuid}`, `PUT /orders/{uuid}/status` | `order:*` |
| **Users** | `GET /users`, `GET /users/{uuid}`, `PATCH /users/{id}/deactivate`, `PATCH /users/{id}/activate` | `user:read`, `user:write` |
| **Coupons** | `GET /coupons`, `POST /coupons`, `PUT /coupons/{uuid}`, `DELETE /coupons/{uuid}` | `coupon:*` |
| **Discounts** | `GET /discounts`, `POST /discounts`, `PUT /discounts/{uuid}`, `DELETE /discounts/{uuid}` | `discount:*` |
| **Offers** | `GET /offers`, `POST /offers`, `PUT /offers/{uuid}`, `DELETE /offers/{uuid}` | `offer:*` |
| **Payments** | `GET /payments`, `GET /payments/{uuid}`, `POST /refunds` | `payment:*` |
| **Shipping** | `GET /deliveries`, `GET /deliveries/{uuid}`, `PUT /deliveries/{uuid}/status`, `GET /shipping-carriers` | `delivery:*`, `shipping:*` |
| **Returns** | `GET /returns`, `GET /returns/{uuid}`, `PUT /returns/{uuid}/status` | `return:*` |
| **Notifications** | `GET /notifications`, `PUT /notifications/{uuid}/read`, `PUT /notifications/read-all` | authenticated |

---

## Admin Role — Backend Permissions

```
ADMIN role has:
  product:read, product:write
  category:read, category:write
  brand:read, brand:write
  tag:read, tag:write
  order:read, order:write, order:update_status
  user:read, user:write
  coupon:read, coupon:write
  discount:read, discount:write
  offer:read, offer:write
  payment:read, payment:write
  shipping:read, shipping:write
  delivery:read, delivery:write
  return:read, return:write
  wallet:read, wallet:write
  currency:read
  chatbot:read, chatbot:write
```

Does **NOT** have access to: `role:*`, `permission:*`, `user_permission:*`, `currency:write`.

---

## Environment Config (same as User App)

Flavor-based `buildConfigField` for `BASE_URL` and `WS_URL`. Default admin credentials should NOT be hardcoded — use login screen.
