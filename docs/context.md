# Design Context & Conventions

## Naming Conventions

### Java
- **Packages**: lowercase, singular (`entity/`, `service/`, `dto/request/`)
- **Classes**: PascalCase (`PermissionService`, `RegisterRequest`)
- **Methods**: camelCase (`getAllRoles`, `createPermission`)
- **Fields**: camelCase (`roleName`, `permissionIds`)
- **Database columns**: snake_case (`role_id`, `permission_name`)

### Module Structure
```
modules/{module}/
├── controller/      — REST controllers
├── dto/
│   ├── request/     — Input DTOs (suffix: Request)
│   └── response/    — Output DTOs (suffix: Response)
├── entity/          — JPA entities
├── exception/       — Module-specific exceptions (extend BaseException)
├── mapper/          — Entity ↔ DTO conversion (static utility class)
├── repository/      — Spring Data JPA repositories
└── service/         — Business logic (with @RequiresPermission on admin methods)
```

### Exception Naming
```
{Entity}NotFoundException           — 404
{Entity}AlreadyExistsException      — 409
{Entity}RequiredException           — 400
{Entity}InUseException              — 409
CannotDeleteProtected{Entity}Exception — 403
Duplicate{Entity}AssignmentException   — 409
InvalidTokenException               — 401
```

All module exceptions extend `core/exception/BaseException`.

## API Response Format

### Success (non-paginated)
```json
{
  "statusCode": 200,
  "message": "Roles retrieved successfully",
  "response": [ ... ]
}
```

### Success (paginated)
```json
{
  "statusCode": 200,
  "message": "Roles retrieved successfully",
  "response": [ ... ],
  "pagination": {
    "currentPage": 0,
    "pageSize": 20,
    "totalElements": 2,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

### Created
```json
{
  "statusCode": 201,
  "message": "Role created successfully",
  "response": { ... }
}
```

### Error
```json
{
  "statusCode": 404,
  "message": "Permission not found with id: 5"
}
```

### Validation Error
```json
{
  "statusCode": 400,
  "message": "Validation failed: roleName - Role name is required",
  "response": []
}
```

## Authorization Convention

Protected service methods are annotated with `@RequiresPermission`:

```java
@Transactional
@RequiresPermission("role:write")
public RolesResponse createRole(RoleRequest request) { ... }
```

The `AuthorizationAspect` intercepts and checks `PermissionService.hasPermission()`.

## Permission Format
Permissions use `resource:action` format:
- `product:read` — read product
- `product:write` — create/edit product
- `product:*` — all product actions
- `*:*` — super admin (all actions on all resources)

## Protected Roles

- **SUPER_ADMIN** — seeded at startup, auto-created with `*:*` permission, CANNOT be deleted
- **USER** — default role for registration, can be deleted (but not recommended)

## Transaction Management
- `@Transactional(readOnly = true)` on all read operations
- `@Transactional` on write operations
- Read methods never modify data

## Optional Fields Convention
- Use `@Nullable` from `jakarta.annotation.Nullable` on optional DTO fields
- Example: `@Nullable private Long roleId;` in RegisterRequest
