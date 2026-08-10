# Enterprise E-Commerce Backend Platform

A comprehensive, production-grade e-commerce backend REST API built with Spring Boot 4.1 and Java 21. Designed with a modular, domain-driven architecture that is microservices-ready, the platform powers a full e-commerce lifecycle — from authentication and product discovery to checkout, payments, shipping, returns, and real-time notifications.

---

## Tech Stack

| Category         | Technology                                      |
| ---------------- | ----------------------------------------------- |
| Framework        | Spring Boot 4.1.0 (Spring Framework 7.0)        |
| Language         | Java 21                                         |
| Build Tool       | Maven                                           |
| Database         | MySQL 9.x / H2 (testing)                        |
| ORM              | Hibernate 7.4 via Spring Data JPA               |
| Security         | Spring Security + JWT (jjwt 0.12.6)             |
| Authorization    | Custom AOP with `@RequiresPermission`           |
| Real-Time        | STOMP over WebSocket                            |
| API Docs         | Springdoc OpenAPI 2.8 (Swagger UI)              |
| Validation       | Jakarta Validation + Hibernate Validator        |
| Monitoring       | Spring Boot Actuator + custom AOP performance   |
| Boilerplate      | Lombok                                          |

---

## Key Achievements

- **Architected & built** a monolithic e-commerce backend with **27 domain modules** following a consistent controller-service-repository-entity-DTO pattern.
- **Designed microservices-ready architecture** using scalar FK references (`Long productId`) instead of cross-module JPA relationships, and event-driven communication via Spring `ApplicationEventPublisher`.
- **Implemented JWT authentication** with access + refresh token pattern, SHA-256 hashing for token storage, automatic rotation on refresh, and scheduled cleanup of expired tokens.
- **Built a custom AOP-based RBAC system** with fine-grained `resource:action` permissions, role-based access, wildcard (`*:*`) support for super-admins, and user-level permission overrides — enforced declaratively via `@RequiresPermission` annotation.
- **Developed real-time push notifications** over STOMP WebSocket with event-driven message delivery, deep linking support, and per-user topic subscriptions.
- **Created a chatbot support system** with a configurable bot-driven Q&A decision tree, automatic escalation to human agents, and real-time chat via WebSocket.
- **Engineered a configurable order state machine** with 8 order statuses, 10 valid transitions, and role-based transition permissions preventing invalid state changes.
- **Implemented dynamic product filtering** using JPA Specifications with category hierarchy resolution (parent + all descendants), attribute matching, price range queries, and full-text search.
- **Built a multi-layered promotions engine** with coupons, discounts, and offers — each supporting global or user-specific assignments, usage limits, minimum order thresholds, and date validity.
- **Integrated wallet management** with credit/debit transactions, balance tracking, and transaction history.
- **Added AOP-based performance monitoring** — every service method is automatically timed with configurable SLOW (>1s) and PERF (>200ms) logging thresholds.
- **Seeded comprehensive reference data** including roles, permissions, order statuses, payment gateways, shipping carriers, currencies, and realistic dummy data (products, users, orders) for development.

---

## Architecture Highlights

### Modular Domain Structure

```
modules/
├── auth/          — Registration, login, OTP, token refresh
├── product/       — Products, specs, dynamic filtering
├── cart/          — Shopping cart management
├── order/         — Orders, order items, status machine
├── payment/       — Payment processing, gateways, refunds
├── shipping/      — Addresses, deliveries, carriers
├── returns/       — Return requests, items, conditions
├── review/        — Reviews, ratings, votes
├── coupon/        — Coupon management & validation
├── discount/      — Discount definitions & assignments
├── offer/         — Offer campaigns & assignments
├── wallet/        — Wallet balance & transactions
├── chat/          — Chat rooms, bot Q&A, agent messaging
├── notification/  — Push notifications & WebSocket delivery
├── user/          — User profiles & management
├── role/          — Role CRUD
├── permission/    — Permission definitions
├── userpermission/— Per-user permission overrides
├── brand/         — Brand CRUD
├── category/      — Hierarchical categories
├── tag/           — Tag CRUD
├── variant/       — Product variants (size, color, etc.)
├── image/         — Product images
├── currency/      — Currency lookup
├── otp/           — OTP generation & verification
├── home/          — Dashboard & home page data
└── ...            — Supporting modules
```

Each module is self-contained with its own **controller**, **service**, **repository**, **entity**, **dto**, **mapper**, and **exception** layers. Modules never import each other's internal packages — only from `core/`.

### Security

- **Authentication**: JWT access token (24h) + refresh token (7d) with rotation
- **Authorization**: AOP-driven `@RequiresPermission("resource:action")` annotation
- **WebSocket**: JWT-validated STOMP connections via custom channel interceptor
- **Stateless**: CSRF disabled, stateless session management

### API Design

- Consistent response format: `ApiResponse<T>` wrapper with `statusCode`, `message`, `response`, and optional `pagination`
- Pagination metadata: `page`, `size`, `totalElements`, `totalPages`
- Context path: `/api/v1`
- Fully documented via Swagger UI at `/api/v1/swagger-ui.html`

### Cross-Module Communication

- **Synchronous**: Scalar FK references (`Long productId`, `Long variantId`) avoid JPA cross-module entity imports
- **Asynchronous**: Spring events (`OrderCreatedEvent`, `PaymentProcessedEvent`, etc.) with `@EventListener` for decoupled side-effects (notifications, logging)

---

## Notable Capabilities

- **40+ REST controllers** covering the full e-commerce domain
- **30+ JPA entities** with proper indexing and relationships
- **Dynamic product recommendations** — similarity scoring by brand, tags, and attributes
- **Configurable promotion stacking** — coupons, discounts, and offers with validation
- **Full WebSocket support** for real-time chat and notifications
- **Comprehensive error handling** — global exception handler with RESTful error codes
- **Dev profile** with realistic seed data for rapid development and testing
- **Dual database support** — MySQL for production, H2 in-memory for tests

---

## Running the Project

```bash
# Prerequisites: Java 21, MySQL 9.x

# Configure database in application.properties
# Run the application
mvn spring-boot:run

# Access Swagger UI
# http://localhost:8083/api/v1/swagger-ui.html
```