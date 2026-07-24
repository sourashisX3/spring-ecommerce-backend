# Development Roadmap

## Phase 0 — Foundation (DONE)
- [x] Project setup (Spring Boot, JPA, MySQL)
- [x] Exception framework (BaseException, GlobalExceptionHandler)
- [x] API response DTO (ApiResponse, Pagination)
- [x] Roles & Permissions CRUD
- [x] Module-specific exceptions
- [x] Validation with `@Valid` + `@Pattern`

## Phase 1 — Auth & Security (IN PROGRESS)
- [ ] Spring Security + JWT integration
- [ ] User registration (`POST /auth/register`)
- [ ] User login (`POST /auth/login`)
- [ ] JWT token filter (OncePerRequestFilter)
- [ ] Secure existing endpoints
- [ ] Fix remaining bugs (UserPermissionService.existsById, PermissionService method ref)
- [ ] Password encryption (BCrypt)

## Phase 2 — Core Ecommerce
- [ ] Product module (CRUD, categories, brands, variants)
- [ ] Search & filter products
- [ ] Shopping cart (add/remove/update items)
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
