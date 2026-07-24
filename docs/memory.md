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
