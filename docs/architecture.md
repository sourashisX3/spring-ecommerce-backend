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
| Validation | Jakarta Validation + Hibernate Validator |
| Lombok | Annotation-based boilerplate reduction |

## Package Structure

```
com.example.ecommerce_backend
├── core/                          # Shared infrastructure
│   ├── config/                    # Spring config classes
│   ├── dto/                       # Shared DTOs (ApiResponse, Pagination)
│   └── exception/                 # BaseException + GlobalExceptionHandler
├── modules/                       # Domain modules
│   ├── role_user/                 # Auth, roles, permissions, users
│   │   ├── controller/
│   │   ├── dto/ (request/, response/)
│   │   ├── entity/
│   │   ├── exception/
│   │   ├── mapper/
│   │   ├── repository/
│   │   └── service/
│   ├── auth/                      # Authentication (JWT, filters)
│   ├── product/                   # Products, categories (future)
│   ├── cart/                      # Shopping cart (future)
│   ├── order/                     # Orders (future)
│   └── payment/                   # Payments (future)
└── EcommerceBackendApplication.java
```

## Module Dependency Rule

Modules NEVER import from each other's service/controller layers.
- Allowed: Module → `core/` (shared DTOs, base exceptions)
- Allowed: Module → itself (entity, service, controller within same package)
- Forbidden: `product/` → `cart/` (use events or shared interfaces instead)

This ensures clean extraction to microservices later.

## API Convention

- Base path: `/api/v1` (via `server.servlet.context-path`)
- Response format: `ApiResponse<T>` — `{ statusCode, message, response, pagination? }`
- Error format: `ApiResponse<?>` — `{ statusCode, message, response? }`
- Validation errors: errors in `message` field, `response` = `[]`

## Entity Relationship

```
User ──→ Role ──→ Permission  (ManyToMany via role_permissions)
User ──→ UserPermission ──→ Permission  (per-user GRANT/DENY overrides)
```

## Key Design Decisions

1. **Context-path versioning** — `/api/v1` via `server.servlet.context-path`, not in controller mappings
2. **Module-specific exceptions** — each module has its own exceptions extending `BaseException`
3. **No cross-module imports** — future-proofed for microservices
4. **Permission names** — `resource:action` format with wildcard `*` support
5. **Role <-> Permission** — ManyToMany with join table, manageable via APIs
