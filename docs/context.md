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
├── exception/       — Module-specific exceptions
├── mapper/          — Entity ↔ DTO conversion (static utility class)
├── repository/      — Spring Data JPA repositories
└── service/         — Business logic
```

### Exception Naming
```
{Entity}NotFoundException     — 404
{Entity}AlreadyExistsException — 409
{Entity}RequiredException     — 400
```

All module exceptions extend `core/exception/BaseException`.

## API Response Format

### Success
```json
{
  "statusCode": 200,
  "message": "Roles retrieved successfully",
  "response": [ ... ]
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

## Permission Format
Permissions use `resource:action` format:
- `product:read` — read product
- `product:write` — create/edit product
- `product:*` — all product actions
- `*:*` — super admin (all actions on all resources)

## Transaction Management
- `@Transactional(readOnly = true)` on all read operations
- `@Transactional` on write operations
- Read methods never modify data
