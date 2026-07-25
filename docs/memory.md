# Decision Log

## 2026-07-24 — Monolithic architecture with modular structure
- **Decision**: Keep monolithic for rapid development, but enforce module boundaries for future microservices extraction.
- **Rationale**: Startup speed > distributed complexity at this stage.

## 2026-07-24 — Merged role + user into single module
- **Decision**: Consolidated `role/` and `user/` into `role_user/` module.
- **Rationale**: User and Role are tightly coupled (User has a Role, UserPermission references both). Early extraction not beneficial.

## 2026-07-24 — URI path versioning via context-path
- **Decision**: Use `server.servlet.context-path=/api/v1` instead of hardcoding version in controller mappings.
- **Rationale**: Controllers are cleaner (`@RequestMapping("/roles")`), and changing version is a single property change.

## 2026-07-24 — Permission names as strings with wildcards
- **Decision**: Use `"resource:action"` format with `*` wildcard support.
- **Rationale**: Human-readable, flexible, no complex hierarchy needed.

## 2026-07-24 — Module-specific exceptions extending BaseException
- **Decision**: Each module defines its own exception classes.
- **Rationale**: Self-contained modules for future extraction; GlobalExceptionHandler catches all via BaseException.

## 2026-07-24 — JWT-based auth with stateless sessions
- **Decision**: Use JWT tokens stored client-side, no server-side session.
- **Rationale**: Stateless, scalable, works well with microservices later.

## 2026-07-24 — Access token + refresh token with rotation
- **Decision**: 24h access token, 7d refresh token; each refresh returns a new pair.
- **Rationale**: Short-lived access tokens reduce exposure; rotation prevents stolen refresh tokens from being reused.

## 2026-07-24 — AOP-based authorization with @RequiresPermission
- **Decision**: Created `@RequiresPermission` annotation + `AuthorizationAspect` for declarative permission checks.
- **Rationale**: Reusable across all future modules; no boilerplate permission checks in each method.

## 2026-07-24 — SUPER_ADMIN seeding + protection
- **Decision**: `DataSeeder` auto-creates `*:*` permission and SUPER_ADMIN role on startup. SUPER_ADMIN cannot be deleted.
- **Rationale**: Ensures there's always an admin account. Prevents accidental lockout.

## 2026-07-24 — Paginated list endpoints
- **Decision**: All GET listing endpoints return Spring Page content with custom Pagination metadata.
- **Rationale**: Standardized pagination across the API; clients can navigate pages consistently.

## 2026-07-24 — Registration roleId as nullable
- **Decision**: `RegisterRequest.roleId` is `@Nullable`. If null, "USER" role is assigned. If provided, validates and assigns that role.
- **Rationale**: Flexibility for admin-created users while keeping registration simple for end users.

## 2026-07-25 — UUID identifiers for all entities
- **Decision**: Added `uuid` field with `@PrePersist` auto-generation to Category, Brand, Tag, ProductVariant, ProductImage entities (Product already had it).
- **Mutation endpoints** use `{uuid}` (e.g., `PUT /categories/{uuid}`, `DELETE /brands/{uuid}`).
- **GET lookups** keep `{slug}` for Category, Brand, Tag (except Product which uses `{uuid}` for both).
- **Rationale**: UUIDs are safe for public exposure (unlike numeric IDs which leak entity count / ordering). Slug-based GET URLs are SEO-friendly and stable.

## 2026-07-25 — Jackson `isXxx` JSON key convention
- **Decision**: All `private boolean isXxx` fields in DTOs use `@JsonProperty("isXxx")` on both getter and setter.
- **Rationale**: Lombok's `@Data` generates getter `isXxx()` for `boolean isXxx` fields. Jackson's Java Bean naming convention strips the `is` prefix from the getter name, producing JSON key `"xxx"` instead of `"isXxx"`. Adding `@JsonProperty` on both getter and setter forces the desired `"isXxx"` key for both serialization and deserialization.
- **Affected**: StatusRequest, VariantRequest, ImageRequest, ProductRequest, BrandResponse, CategoryResponse, ImageResponse, TagResponse, ProductResponse, VariantResponse, UserResponse.

## 2026-07-25 — Variants created via separate API, not inline in ProductRequest
- **Decision**: Variant creation is via `POST /products/{productUuid}/variants`, not as an inline list in ProductRequest.
- **Rationale**: Keeps product creation simple; variants are optional and added later. Avoids complex nested validation.
