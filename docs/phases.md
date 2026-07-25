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
- `Product` — uuid, name, description, price, attributes(JSON), isActive, isFeatured, timestamps
- `Category` — uuid, name, slug, description, parent (self-ref), sortOrder, isActive, timestamps
- `Brand` — uuid, name, slug, description, logoUrl, website, isActive, timestamps
- `Tag` — uuid, name, slug, isActive, timestamps
- `ProductVariant` — uuid, sku, name, price, stock, attributes(JSON), isActive, isDefault, sortOrder, timestamps
- `ProductImage` — uuid, imageUrl, isPrimary, sortOrder (belongs to Product, optionally to Variant)

### Identifier Convention
- **GET lookups**: `{slug}` (e.g., `/categories/{slug}`, `/brands/{slug}`)
- **Mutations**: `{uuid}` (e.g., `PUT /categories/{uuid}`, `DELETE /brands/{uuid}`, `PATCH /tags/{uuid}/status`)
- **Products**: `{uuid}` for both GET and mutations (`/products/{uuid}`)
- **Variants/Images**: `{uuid}` for mutations (`/variants/{variantUuid}`, `/images/{imageUuid}`)
- **Create endpoints**: POST to collection (no identifier in URL), UUID auto-generated via @PrePersist

### Jackson `isXxx` Convention
All `private boolean isXxx` fields in DTOs use `@JsonProperty("isXxx")` on both getter and setter to force JSON key to `"isXxx"` (prevents Lombok + Java Bean convention from stripping the `is` prefix).

Affected DTOs:
- **Request**: `StatusRequest` (isActive), `VariantRequest` (isDefault), `ImageRequest` (isPrimary), `ProductRequest` (isFeatured)
- **Response**: `BrandResponse` (isActive), `CategoryResponse` (isActive), `TagResponse` (isActive), `ImageResponse` (isPrimary), `ProductResponse` (isActive, isFeatured), `VariantResponse` (isActive, isDefault), `UserResponse` (isActive, isEmailVerified, isPhoneVerified)

### APIs
```
# Products
GET    /products                           — list with filters, paginated
GET    /products/{uuid}                    — single product with optional attributeFilters
POST   /products                           — create (@RequiresPermission("product:write"))
PUT    /products/{uuid}                    — update (@RequiresPermission("product:write"))
PATCH  /products/{uuid}/status             — toggle active/inactive
DELETE /products/{uuid}                    — delete
GET    /products/{uuid}/similar            — similar products by category/tags

# Categories
GET    /categories                         — all categories (optional ?active filter)
GET    /categories/tree                    — hierarchical tree
GET    /categories/{slug}                  — by slug
POST   /categories                         — create (@RequiresPermission("category:write"))
PUT    /categories/{uuid}                  — update (@RequiresPermission("category:write"))
PATCH  /categories/{uuid}/status           — toggle active/inactive
DELETE /categories/{uuid}                  — delete (checks children + products)

# Brands
GET    /brands                             — all brands (optional ?active filter)
GET    /brands/{slug}                      — by slug
POST   /brands                             — create (@RequiresPermission("brand:write"))
PUT    /brands/{uuid}                      — update (@RequiresPermission("brand:write"))
PATCH  /brands/{uuid}/status               — toggle active/inactive
DELETE /brands/{uuid}                      — delete

# Tags
GET    /tags                               — all tags (optional ?active filter)
POST   /tags                               — create (@RequiresPermission("tag:write"))
PATCH  /tags/{uuid}/status                 — toggle active/inactive
DELETE /tags/{uuid}                        — delete

# Variants
POST   /products/{productUuid}/variants    — add variant
PUT    /variants/{variantUuid}             — update variant
DELETE /variants/{variantUuid}             — delete variant

# Images
POST   /products/{productUuid}/images      — add image
DELETE /images/{imageUuid}                 — delete image (assigns new primary if was primary)
```

### Product Filtering
- `categorySlug` — filter by category
- `brandSlug` — filter by brand
- `tagSlug` — filter by tag
- `search` — full-text search on name/slug/description
- `minPrice`, `maxPrice` — price range filter (considers variant prices)
- `isFeatured` — filter featured products
- `active` — filter by active status (default: true)
- `sortBy` + `sortDir` — sorting (default: name ASC)
- `attrColor`, `attrSize`, `attrMaterial` — attribute-based filtering

## Phase 2 — Cart Module (TODO)

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
