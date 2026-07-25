# Development Roadmap

## Phase 0 — Foundation (DONE)
- [x] Project setup (Spring Boot, JPA, MySQL)
- [x] Exception framework (BaseException, GlobalExceptionHandler)
- [x] API response DTO (ApiResponse, Pagination)
- [x] Roles & Permissions CRUD
- [x] Module-specific exceptions
- [x] Validation with `@Valid` + `@Pattern`

## Phase 1 — Auth & Security (DONE)
- [x] Spring Security + JWT integration
- [x] User registration (`POST /auth/register`) with nullable roleId
- [x] User login (`POST /auth/login`)
- [x] JWT token filter (OncePerRequestFilter)
- [x] Access token (24h) + refresh token (7d) with rotation
- [x] Token refresh endpoint (`POST /auth/refresh`)
- [x] Password encryption (BCrypt)
- [x] AOP-based authorization (@RequiresPermission annotation)
- [x] SUPER_ADMIN seeding at startup with `*:*` permission
- [x] SUPER_ADMIN deletion protection
- [x] Environment-specific exception handling (BadCredentials, DataIntegrity, UsernameNotFoundException)
- [x] All listing endpoints paginated
- [x] User CRUD (GET /users, GET /users/{id}, DELETE /users/{id})
- [x] Permission-in-use check before delete
- [x] Duplicate user-permission assignment check

## Phase 2 — Core Ecommerce
- [x] Product module (CRUD, categories, brands, variants, images)
- [x] Search & filter products
- [x] UUID identifiers for all entities
- [x] Jackson isXxx JSON key consistency
- [ ] Shopping cart (add/remove/update items)
- [ ] Reviews & ratings
- [ ] Wishlist
- [ ] Conditional pagination (paginated if page/size present, else full list)
- [ ] User address management
- [ ] Order placement flow
- [ ] Order tracking & history
- [ ] Payment integration
- [ ] Inventory management

## Phase 3 — Quick Commerce (Future)
- [ ] Hyperlocal delivery zones
- [ ] Delivery time slots
- [ ] Real-time order tracking
- [ ] Warehouse management
- [ ] Rider/delivery partner module

## Phase 4 — Microservices Extraction (Future)
- [ ] Extract auth service
- [ ] Extract product service
- [ ] Extract order service
- [ ] API Gateway
- [ ] Event-driven communication
