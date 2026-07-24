# Phase Details

## Phase 1 — Auth & Security

### Dependencies to add
```xml
spring-boot-starter-security
io.jsonwebtoken:jjwt-api:0.12.6
io.jsonwebtoken:jjwt-impl:0.12.6 (runtime)
io.jsonwebtoken:jjwt-jackson:0.12.6 (runtime)
```

### Files to create
| File | Purpose |
|------|---------|
| `core/config/SecurityConfig.java` | Spring Security filter chain |
| `core/config/JwtTokenProvider.java` | JWT generation & validation |
| `core/config/JwtAuthenticationFilter.java` | Reads JWT from Authorization header |
| `core/config/CustomUserDetailsService.java` | Loads user by email for authentication |
| `modules/role_user/dto/request/RegisterRequest.java` | Registration input |
| `modules/role_user/dto/request/LoginRequest.java` | Login input |
| `modules/role_user/dto/response/AuthResponse.java` | Token + user info |
| `modules/role_user/dto/response/UserResponse.java` | User profile output |
| `modules/role_user/dto/request/AddressRequest.java` | Address input for registration |
| `modules/role_user/service/AuthService.java` | Register + login business logic |
| `modules/role_user/controller/AuthController.java` | Auth endpoints |

### Endpoints
```
POST /auth/register  — public, creates user + returns JWT
POST /auth/login     — public, validates credentials + returns JWT
POST /auth/refresh   — public (or protected), refreshes JWT
```

### Security rules
- `/auth/**` — permit all
- `/roles/**` — permit all (admin later)
- `/permissions/**` — permit all (admin later)
- Everything else — requires authentication (future)

## Phase 2 — Product Module

### Entities
- `Product` — name, description, price, images, status
- `Category` — name, description, parent category
- `Brand` — name, logo
- `ProductVariant` — size, color, SKU, price override

### APIs
```
GET    /products                  — list with filters
GET    /products/{id}             — single product
POST   /products                  — create (admin)
PUT    /products/{id}             — update (admin)
DELETE /products/{id}             — delete (admin)
GET    /categories                — all categories
POST   /categories                — create (admin)
```

## Phase 2 — Cart Module

### Entities
- `Cart` — userId, status
- `CartItem` — productId, variantId, quantity

### APIs
```
GET    /cart               — current user's cart
POST   /cart/items         — add item
PUT    /cart/items/{id}    — update quantity
DELETE /cart/items/{id}    — remove item
DELETE /cart               — clear cart
```
