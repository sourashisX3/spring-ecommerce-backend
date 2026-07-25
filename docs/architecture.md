# Architecture

## Overview

Monolithic Spring Boot ecommerce backend designed for future microservices extraction. Each domain module is self-contained with its own entities, services, controllers, DTOs, and exceptions.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 4.1.0 (Spring 7.0.8) |
| Language | Java 21 |
| Database | MySQL 9.x |
| ORM | Hibernate 7.4.1 |
| Build | Maven |
| Auth | JWT (jjwt) |
| Authorization | AOP + @RequiresPermission annotation |
| Validation | Jakarta Validation + Hibernate Validator |
| Lombok | Annotation-based boilerplate reduction |
| AOP | aspectjweaver |
| WebSocket | STOMP over WebSocket (spring-boot-starter-websocket + spring-messaging) |
| API Docs | springdoc-openapi (Swagger UI) |

## Package Structure

```
com.example.ecommerce_backend
├── core/                              # Shared infrastructure
│   ├── annotation/                    # Custom annotations (@RequiresPermission)
│   ├── aspect/                        # AOP aspects (AuthorizationAspect)
│   ├── config/                        # Spring config (Security, JWT, DataSeeder, Async)
│   ├── dto/                           # Shared DTOs (ApiResponse, Pagination)
│   ├── entity/                        # Shared entities (RefreshToken)
│   ├── event/                         # Domain events (OrderCreated, PaymentProcessed, etc.)
│   ├── exception/                     # BaseException + GlobalExceptionHandler
│   └── service/                       # Shared services (RefreshTokenService)
├── modules/                           # Domain modules
│   ├── auth/                          # Authentication (register, login, refresh, logout, OTP)
│   ├── brand/                         # Brand CRUD
│   ├── cart/                          # Shopping cart
│   ├── category/                      # Category CRUD (hierarchical)
│   ├── chat/                          # Support chat with bot triage + human escalation
│   ├── coupon/                        # Coupon management + validation
│   ├── currency/                      # Currency lookup table
│   ├── discount/                      # Discount definitions + assignments
│   ├── image/                         # Product image management
│   ├── notification/                  # Notifications with WebSocket push + deep links
│   ├── offer/                         # Offer definitions + assignments
│   ├── order/                         # Orders, order items, status history
│   ├── otp/                           # OTP generation + verification
│   ├── payment/                       # Payment processing + refunds
│   ├── permission/                    # Permission CRUD
│   ├── product/                       # Products, variants
│   ├── returns/                       # Return requests + items
│   ├── review/                        # Reviews + votes
│   ├── role/                          # Role CRUD
│   ├── shipping/                      # Addresses, deliveries, carriers
│   ├── tag/                           # Tag CRUD
│   ├── user/                          # User profile management
│   ├── userpermission/                # Per-user permission overrides
│   ├── variant/                       # Product variant management
│   ├── wallet/                        # Wallet balance + transactions
│   └── wishlist/                      # Wishlist management
└── EcommerceBackendApplication.java
```

## Module Dependency Rule

Modules NEVER import from each other's service/controller layers.
- Allowed: Module → `core/` (shared DTOs, base exceptions, annotations, aspects, events)
- Allowed: Module → itself (entity, service, controller within same package)
- Forbidden: `product/` → `cart/` (use events or shared interfaces instead)

This ensures clean extraction to microservices later.

## Cross-Module Communication

### Synchronous: Scalar FK References
OrderItem stores `Long productId` and `Long variantId` instead of `@ManyToOne Product`. This avoids importing Product entity into Order module — a deliberate microservices-friendly design.

### Asynchronous: Spring Events
Domain events decouple modules. When an order is created, `OrderService` publishes an `OrderCreatedEvent`. `NotificationService` listens via `@EventListener` and pushes a WebSocket notification — no direct dependency from `order` to `notification`.

This is the **only** cross-module communication pattern allowed.

## API Convention

- Base path: `/api` (via `server.servlet.context-path=/api`)
- Response format: `ApiResponse<T>` — `{ statusCode, message, response, pagination? }`
- Error format: `ApiResponse<?>` — `{ statusCode, message, response? }`
- Validation errors: errors in `message` field, `response` = `[]`
- Paginated responses: `response` = content array, `pagination` = metadata block

## WebSocket Architecture

### Connection Flow
1. Client connects to `ws://host/ws?token=JWT`
2. `JwtChannelInterceptor` extracts JWT from query param
3. Token validated via `JwtTokenProvider` → `Principal` set in session
4. STOMP session established with authenticated user

### Notification Delivery
1. A domain event is published (e.g., `OrderCreatedEvent`)
2. `NotificationService` receives event via `@EventListener`
3. Notification persisted to `notifications` table
4. `SimpMessagingTemplate.convertAndSendToUser()` pushes via STOMP
5. Client subscribed to `/user/queue/notifications` receives real-time push

### Chat Triage Flow
1. User opens support → `ChatService.createRoom()` → status `BOT_ACTIVE`
2. `ChatBotService` serves predefined Q&A tree from `chat_bot_questions` table
3. Bot sends questions with quick-reply options
4. User selects options → bot navigates the question tree
5. At escalation point, user requests human agent → status `AWAITING_AGENT`
6. Support agent picks up → status `ACTIVE` → real-time chat via STOMP
7. All messages persisted in `chat_messages` table

### STOMP Destinations

| Direction | Destination | Purpose |
|---|---|---|
| Connect | `/ws?token=JWT` | WebSocket handshake with auth |
| Subscribe | `/user/queue/notifications` | Personal notifications |
| Subscribe | `/topic/admin/notifications` | Admin broadcast |
| Send | `/app/chat/create` | Create support room |
| Send | `/app/chat/room/{uuid}/send` | Send message to room |
| Send | `/app/chat/room/{uuid}/typing` | Typing indicator |
| Send | `/app/chat/room/{uuid}/assign` | Agent assigns self to room |
| Send | `/app/chat/room/{uuid}/close` | Close room |
| Subscribe | `/topic/chat/room/{uuid}` | Room message broadcast |
| Subscribe | `/user/queue/chat/events` | Room lifecycle events |

## Notification Deep Links

```json
{
  "type": "ORDER_CONFIRMED",
  "title": "Order Confirmed",
  "body": "Your order #ORD-AB12 has been placed",
  "deepLink": "ecommerce://orders/abc-123-def"
}
```

Supported deep link patterns:

| Type | Deep Link |
|------|-----------|
| Order confirmed / status change | `ecommerce://orders/{uuid}` |
| Payment received / failed | `ecommerce://orders/{uuid}` |
| Delivery updated | `ecommerce://orders/{uuid}` |
| Return status change | `ecommerce://returns/{uuid}` |
| New chat message | `ecommerce://chat/rooms/{uuid}` |
| Admin broadcast | `ecommerce://` |

Web fallback: `https://app.example.com/orders/{uuid}`

## Event Model

| Event | Publisher | Consumer |
|-------|-----------|----------|
| `OrderCreatedEvent` | OrderService | NotificationService |
| `OrderStatusChangedEvent` | OrderStatusService | NotificationService |
| `PaymentProcessedEvent` | PaymentService | NotificationService |
| `PaymentFailedEvent` | PaymentService | NotificationService |
| `DeliveryStatusChangedEvent` | DeliveryService | NotificationService |
| `UserRegisteredEvent` | AuthService | NotificationService |

## Authorization System

- `@RequiresPermission("resource:action")` annotation on service methods
- `AuthorizationAspect` (AOP) intercepts annotated methods
- Extracts current user from SecurityContext → calls `PermissionService.hasPermission()`
- Supports `*:*` wildcard (SUPER_ADMIN bypasses all checks)
- Throws `PermissionRequiredException` (403) if denied

## Key Design Decisions

1. **Context-path versioning** — `/api` via `server.servlet.context-path`, versioning via future `/api/v1`
2. **Module-specific exceptions** — each module has its own exceptions extending `BaseException`
3. **No cross-module imports** — future-proofed for microservices
4. **Permission names** — `resource:action` format with wildcard `*` support
5. **Role ↔ Permission** — ManyToMany with join table, manageable via APIs
6. **Access + Refresh tokens** — 24h access token, 7d refresh token with rotation
7. **SUPER_ADMIN seeding** — auto-created at startup with `*:*` permission, protected from deletion
8. **Protected roles** — SUPER_ADMIN cannot be deleted (throws 403)
9. **Paginated listings** — all GET list endpoints return paginated responses with `Pagination` metadata
10. **Scalar FK references** — modules reference each other via `Long` IDs, not JPA relationships (microservices-friendly)
11. **Event-driven notifications** — Spring events decouple business logic from notification delivery
12. **Bot-first support** — chat triage uses configurable Q&A tree before escalating to human agents
13. **Deep link notifications** — every notification carries a deep link for native navigation in KMP apps
