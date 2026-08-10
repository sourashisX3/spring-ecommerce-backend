# E-Commerce — Android Super Admin App

> **Target:** Android Mobile + Tablet (Super Admins)  
> **Architecture:** MVVM + Clean Architecture  
> **Tech:** Jetpack Compose, Hilt, Room, Retrofit, Kotlin Serialization, Coil  
> **Base URL:** `http://host:8083/api/v1`

---

## Project Structure

```
app/src/main/java/com/ecommerce/superadmin/
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
│   │       ├── SuperAdminTopBar.kt
│   │       ├── PermissionChip.kt
│   │       ├── DataTable.kt
│   │       ├── ConfirmDialog.kt
│   │       ├── EmptyState.kt
│   │       └── LoadingIndicator.kt
│   └── utils/
│       ├── Constants.kt
│       ├── Extensions.kt
│       └── PermissionUtils.kt
└── feature/                              # Inherits ALL admin features +
    ├── roles/                            # SUPER-ADMIN ONLY
    │   ├── data/
    │   │   ├── datasource/remote/
    │   │   │   └── RoleApi.kt
    │   │   ├── dto/
    │   │   │   ├── RoleResponse.kt
    │   │   │   └── RoleRequest.kt
    │   │   ├── mapper/
    │   │   │   └── RoleMapper.kt
    │   │   └── repository/
    │   │       └── RoleRepositoryImpl.kt
    │   ├── domain/
    │   │   ├── model/
    │   │   │   └── Role.kt
    │   │   ├── repository/
    │   │   │   └── RoleRepository.kt
    │   │   └── usecase/
    │   │       ├── GetRolesUseCase.kt
    │   │       ├── CreateRoleUseCase.kt
    │   │       └── DeleteRoleUseCase.kt
    │   └── presentation/
    │       ├── components/
    │       │   ├── RoleCard.kt
    │       │   ├── PermissionCheckboxGroup.kt
    │       │   └── RoleFormDialog.kt
    │       ├── events/
    │       │   └── RoleEvent.kt
    │       ├── screens/
    │       │   └── RoleManagementScreen.kt
    │       ├── states/
    │       │   └── RoleState.kt
    │       └── viewmodels/
    │           └── RoleViewModel.kt
    ├── permissions/                      # SUPER-ADMIN ONLY
    │   ├── data/
    │   │   ├── datasource/remote/
    │   │   │   └── PermissionApi.kt
    │   │   ├── dto/
    │   │   │   ├── PermissionResponse.kt
    │   │   │   └── CreatePermissionRequest.kt
    │   │   ├── mapper/
    │   │   │   └── PermissionMapper.kt
    │   │   └── repository/
    │   │       └── PermissionRepositoryImpl.kt
    │   ├── domain/
    │   │   ├── model/
    │   │   │   └── Permission.kt
    │   │   ├── repository/
    │   │   │   └── PermissionRepository.kt
    │   │   └── usecase/
    │   │       ├── GetPermissionsUseCase.kt
    │   │       ├── CreatePermissionUseCase.kt
    │   │       └── DeletePermissionUseCase.kt
    │   └── presentation/
    │       ├── components/
    │       │   ├── PermissionTable.kt
    │       │   └── CreatePermissionDialog.kt
    │       ├── events/
    │       │   └── PermissionEvent.kt
    │       ├── screens/
    │       │   └── PermissionManagementScreen.kt
    │       ├── states/
    │       │   └── PermissionState.kt
    │       └── viewmodels/
    │           └── PermissionViewModel.kt
    ├── userpermissions/                  # SUPER-ADMIN ONLY
    │   ├── data/
    │   │   ├── datasource/remote/
    │   │   │   └── UserPermissionApi.kt
    │   │   ├── dto/
    │   │   │   └── UserPermissionResponse.kt
    │   │   ├── mapper/
    │   │   │   └── UserPermissionMapper.kt
    │   │   └── repository/
    │   │       └── UserPermissionRepositoryImpl.kt
    │   ├── domain/
    │   │   ├── model/
    │   │   │   └── UserPermission.kt
    │   │   ├── repository/
    │   │   │   └── UserPermissionRepository.kt
    │   │   └── usecase/
    │   │       ├── GetUserPermissionsUseCase.kt
    │   │       ├── AssignUserPermissionUseCase.kt
    │   │       └── RemoveUserPermissionUseCase.kt
    │   └── presentation/
    │       ├── components/
    │       │   ├── UserPermissionList.kt
    │       │   └── AssignPermissionDialog.kt
    │       ├── events/
    │       │   └── UserPermissionEvent.kt
    │       ├── screens/
    │       │   └── UserPermissionManagementScreen.kt
    │       ├── states/
    │       │   └── UserPermissionState.kt
    │       └── viewmodels/
    │           └── UserPermissionViewModel.kt
    ├── currencies/                       # SUPER-ADMIN ONLY
    │   ├── data/
    │   │   ├── datasource/remote/
    │   │   │   └── CurrencyApi.kt
    │   │   ├── dto/
    │   │   │   ├── CurrencyResponse.kt
    │   │   │   └── CurrencyRequest.kt
    │   │   ├── mapper/
    │   │   │   └── CurrencyMapper.kt
    │   │   └── repository/
    │   │       └── CurrencyRepositoryImpl.kt
    │   ├── domain/
    │   │   ├── model/
    │   │   │   └── Currency.kt
    │   │   ├── repository/
    │   │   │   └── CurrencyRepository.kt
    │   │   └── usecase/
    │   │       ├── GetCurrenciesUseCase.kt
    │   │       ├── CreateCurrencyUseCase.kt
    │   │       ├── UpdateCurrencyUseCase.kt
    │   │       └── DeleteCurrencyUseCase.kt
    │   └── presentation/
    │       ├── components/
    │       │   └── CurrencyFormDialog.kt
    │       ├── events/
    │       │   └── CurrencyEvent.kt
    │       ├── screens/
    │       │   └── CurrencyManagementScreen.kt
    │       ├── states/
    │       │   └── CurrencyState.kt
    │       └── viewmodels/
    │           └── CurrencyViewModel.kt
    ├── otp/                              # SUPER-ADMIN ONLY
    │   ├── data/
    │   │   ├── datasource/remote/
    │   │   │   └── OtpApi.kt
    │   │   ├── dto/
    │   │   │   └── OtpResponse.kt
    │   │   └── repository/
    │   │       └── OtpRepositoryImpl.kt
    │   ├── domain/
    │   │   ├── model/
    │   │   │   └── OtpRecord.kt
    │   │   ├── repository/
    │   │   │   └── OtpRepository.kt
    │   │   └── usecase/
    │   │       ├── GetOtpLogsUseCase.kt
    │   │       └── InvalidateOtpUseCase.kt
    │   └── presentation/
    │       ├── screens/
    │       │   └── OtpLogsScreen.kt
    │       └── viewmodels/
    │           └── OtpLogsViewModel.kt
    └── system/                           # SUPER-ADMIN ONLY
        └── presentation/
            ├── screens/
            │   └── SystemConfigScreen.kt
            └── viewmodels/
                └── SystemConfigViewModel.kt
```

---

## Dependencies (Same as User/Admin App)

```kotlin
// Identical base dependencies to user-app-android.md
// No additional admin-specific dependencies needed
```

---

## Navigation Structure (Super Admin)

```
SuperAdminNavGraph
├── AuthGraph
│   └── SuperAdminLogin
└── MainGraph
    ├── Dashboard (bottom nav) — full stats
    ├── Products (bottom nav) — full CRUD
    ├── Orders (bottom nav) — full management
    ├── Users (bottom nav)
    │   ├── UserList
    │   ├── UserDetail/{uuid}
    │   └── UserPermissions/{uuid}       # GRANT/DENY overrides
    ├── More (bottom nav)
    │   ├── Roles                         # CRUD roles
    │   ├── Permissions                   # CRUD permissions
    │   ├── Coupons
    │   ├── Discounts
    │   ├── Offers
    │   ├── Payments
    │   ├── Returns
    │   ├── Shipping / Deliveries
    │   ├── Currencies                    # CRUD currencies
    │   ├── OTP Logs
    │   └── System Config
    └── Profile
```

---

## API Endpoints Used (Super Admin — Exclusive)

| Feature | Endpoints | Permissions |
|---|---|---|
| **Roles** | `GET /roles`, `POST /roles`, `DELETE /roles/{id}` | `role:*` |
| **Permissions** | `GET /permissions`, `POST /permissions`, `DELETE /permissions/{id}` | `permission:*` |
| **User Permissions** | `GET /users/{id}/permissions`, `POST /user-permissions`, `DELETE /user-permissions/{id}` | `user_permission:*` |
| **Currencies** | `GET /currencies`, `POST /currencies`, `PUT /currencies/{uuid}`, `DELETE /currencies/{uuid}` | `currency:*` (write) |
| **OTP** | `GET /otp/logs`, `POST /otp/invalidate` | `otp:*` |
| **System** | `GET /actuator/health`, custom config endpoints | `system:*` |

For all **admin-level** endpoints (products, categories, orders, etc.), see `admin-app-android.md`.

---

## SUPER_ADMIN Role — Backend Permissions

```
SUPER_ADMIN role has: 
  *:*   (all permissions — 40+ resources × read/write)
```

This means every single endpoint in the backend is accessible. The super-admin app UI should conditionally render all management features.

---

## Key Differences from Admin App

| Area | Admin | Super Admin |
|---|---|---|
| **User management** | List, deactivate/activate only | Full + permission overrides |
| **Roles** | View only (if exposed) | CRUD |
| **Permissions** | View only (if exposed) | CRUD |
| **Currencies** | Read-only | CRUD |
| **OTP** | Not available | Logs + invalidate |
| **System config** | Not available | Health check, config |
| **User-level perms** | Not available | GRANT/DENY overrides per user |
| **Navigation** | Bottom nav: Dashboard, Products, Orders, More | Bottom nav: Dashboard, Products, Orders, Users, More |
