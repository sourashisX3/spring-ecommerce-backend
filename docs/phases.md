# Phase Details

## Phase 1 — Auth & Security (COMPLETE)

### Dependencies added
```xml
spring-boot-starter-security
io.jsonwebtoken:jjwt-api:0.12.6
io.jsonwebtoken:jjwt-impl:0.12.6 (runtime)
io.jsonwebtoken:jjwt-jackson:0.12.6 (runtime)
org.aspectj:aspectjweaver:1.9.22.1
```

### Files created

#### core/ (shared infrastructure)
| File | Purpose |
|------|---------|
| `core/annotation/RequiresPermission.java` | `@RequiresPermission("permission:write")` annotation |
| `core/aspect/AuthorizationAspect.java` | AOP aspect — checks permission from SecurityContext |
| `core/config/SecurityConfig.java` | Spring Security filter chain, stateless, CSRF disabled |
| `core/config/JwtTokenProvider.java` | JWT generation (access + refresh) & validation |
| `core/config/JwtAuthenticationFilter.java` | Extracts JWT from Authorization header, sets SecurityContext |
| `core/config/CustomUserDetailsService.java` | Loads user by email for Spring Security authentication |
| `core/config/DataSeeder.java` | ApplicationRunner — seeds `*:*` permission + SUPER_ADMIN role at startup |

#### modules/role_user/ (domain)
| File | Purpose |
|------|---------|
| `dto/request/RegisterRequest.java` | Registration input (firstName, lastName, email, phoneNumber, password, @Nullable roleId, address) |
| `dto/request/LoginRequest.java` | Login input (email, password) |
| `dto/request/RefreshTokenRequest.java` | Refresh token input |
| `dto/response/AuthResponse.java` | Token + refreshToken + user info |
| `dto/response/UserResponse.java` | User profile output (includes createdAt, updatedAt) |
| `exception/EmailAlreadyExistsException.java` | 409 Conflict |
| `exception/InvalidTokenException.java` | 401 Unauthorized |
| `exception/CannotDeleteProtectedRoleException.java` | 403 Forbidden — SUPER_ADMIN cannot be deleted |
| `exception/DuplicatePermissionAssignmentException.java` | 409 Conflict |
| `exception/PermissionInUseException.java` | 409 Conflict — permission referenced by roles/users |
| `service/AuthService.java` | Register, login, refresh with JWT generation |
| `controller/AuthController.java` | POST /auth/register, /auth/login, /auth/refresh |
| `controller/UserController.java` | GET /users (paginated), GET /users/{id}, DELETE /users/{id} |
| `mapper/UserMapper.java` | Entity → UserResponse conversion |

### Endpoints
```
POST /auth/register     — public, creates user + returns JWT (nullable roleId, defaults to USER)
POST /auth/login        — public, validates credentials + returns JWT
POST /auth/refresh      — public, rotates access + refresh tokens
GET  /roles             — paginated, public
POST /roles             — requires role:write
DELETE /roles/{id}      — requires role:write (SUPER_ADMIN protected)
GET  /permissions       — paginated, public
POST /permissions       — requires permission:write
DELETE /permissions/{id} — requires permission:write (checks in-use)
GET  /users             — paginated, public
GET  /users/{id}        — public
DELETE /users/{id}      — public
GET  /users/{id}/permissions — paginated, public
POST /users/{id}/permissions — requires user_permission:write (duplicate check → 409)
DELETE /users/{id}/permissions/{pid} — requires user_permission:write
```

### Authorization rules
- `@RequiresPermission` on service methods (AOP-enforced)
- SUPER_ADMIN role seeded with `*:*` wildcard — bypasses all checks
- Protected roles: SUPER_ADMIN (cannot be deleted)
- Registration always assigns "USER" role by default; optional `roleId` overrides

### Pagination
All GET list endpoints accept `?page=0&size=20&sort=id,asc` with defaults:
```
response: [ ... ]        // content array (not Spring Page object)
pagination: {
  currentPage, pageSize, totalElements, totalPages, hasNext, hasPrevious
}
```

## Phase 2 — Product Module

### Entities
- `Product` — name, description, price, images, status
- `Category` — name, description, parent category
- `Brand` — name, logo
- `ProductVariant` — size, color, SKU, price override

### APIs
```
GET    /products                  — list with filters (paginated)
GET    /products/{id}             — single product
POST   /products                  — create (@RequiresPermission("product:write"))
PUT    /products/{id}             — update (@RequiresPermission("product:write"))
DELETE /products/{id}             — delete (@RequiresPermission("product:write"))
GET    /categories                — all categories
POST   /categories                — create (@RequiresPermission("product:write"))
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
