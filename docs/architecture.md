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

## Package Structure

```
com.example.ecommerce_backend
├── core/                          # Shared infrastructure
│   ├── annotation/                # Custom annotations (@RequiresPermission)
│   ├── aspect/                    # AOP aspects (AuthorizationAspect)
│   ├── config/                    # Spring config (Security, JWT, DataSeeder)
│   ├── dto/                       # Shared DTOs (ApiResponse, Pagination)
│   └── exception/                 # BaseException + GlobalExceptionHandler
├── modules/                       # Domain modules
│   ├── product/                    # Products, categories, brands, tags, variants, images
│   │   ├── controller/             # ProductController, CategoryController, BrandController, TagController, VariantController, ImageController
│   │   ├── dto/ (request/, response/)
│   │   ├── entity/                 # Product, Category, Brand, Tag, ProductVariant, ProductImage
│   │   ├── exception/              # 6 module-specific exceptions
│   │   ├── mapper/                 # ProductMapper, CategoryMapper, BrandMapper, TagMapper
│   │   ├── repository/             # 6 Spring Data repositories
│   │   └── service/                # ProductService, CategoryService, BrandService, TagService
│   └── role_user/                 # Auth, roles, permissions, users
│       ├── controller/            # AuthController, RolesController, PermissionController, UserPermissionController, UserController
│       ├── dto/ (request/, response/)
│       ├── entity/                # User, Role, Permission, UserAddress, UserPermission
│       ├── exception/             # 12 module-specific exceptions
│       ├── mapper/                # RolesMapper, UserMapper
│       ├── repository/            # 4 Spring Data repositories
│       └── service/               # AuthService, RolesService, PermissionService, UserPermissionService, UserService
└── EcommerceBackendApplication.java
```

## Module Dependency Rule

Modules NEVER import from each other's service/controller layers.
- Allowed: Module → `core/` (shared DTOs, base exceptions, annotations, aspects)
- Allowed: Module → itself (entity, service, controller within same package)
- Forbidden: `product/` → `cart/` (use events or shared interfaces instead)

This ensures clean extraction to microservices later.

## API Convention

- Base path: `/api/v1` (via `server.servlet.context-path`)
- Response format: `ApiResponse<T>` — `{ statusCode, message, response, pagination? }`
- Error format: `ApiResponse<?>` — `{ statusCode, message, response? }`
- Validation errors: errors in `message` field, `response` = `[]`
- Paginated responses: `response` = content array, `pagination` = metadata block

## Entity Relationship

```
User ──→ Role ──→ Permission  (ManyToMany via role_permissions)
User ──→ UserPermission ──→ Permission  (per-user GRANT/DENY overrides)

Product ──→ Category  (ManyToOne)
Product ──→ Brand     (ManyToOne)
Product ──→ Tag       (ManyToMany via product_tags)
Product ──→ ProductVariant  (OneToMany)
Product ──→ ProductImage    (OneToMany)
ProductImage ──→ ProductVariant (ManyToOne, optional — image scoped to variant)
```

## UUID Usage
- All entities have `uuid` field with `@Column(unique = true, nullable = false)`
- Generated via `@PrePersist`: `this.uuid = UUID.randomUUID().toString();`
- `Long id` (auto-increment) kept as internal primary key; `uuid` used for external API lookups

## Authorization System

- `@RequiresPermission("resource:action")` annotation on service methods
- `AuthorizationAspect` (AOP) intercepts annotated methods
- Extracts current user from SecurityContext → calls `PermissionService.hasPermission()`
- Supports `*:*` wildcard (SUPER_ADMIN bypasses all checks)
- Throws `PermissionRequiredException` (403) if denied

## Key Design Decisions

1. **Context-path versioning** — `/api/v1` via `server.servlet.context-path`, not in controller mappings
2. **Module-specific exceptions** — each module has its own exceptions extending `BaseException`
3. **No cross-module imports** — future-proofed for microservices
4. **Permission names** — `resource:action` format with wildcard `*` support
5. **Role <-> Permission** — ManyToMany with join table, manageable via APIs
6. **Access + Refresh tokens** — 24h access token, 7d refresh token with rotation
7. **SUPER_ADMIN seeding** — auto-created at startup with `*:*` permission, protected from deletion
8. **Protected roles** — SUPER_ADMIN cannot be deleted (throws 403)
9. **Paginated listings** — all GET list endpoints return paginated responses with `Pagination` metadata
