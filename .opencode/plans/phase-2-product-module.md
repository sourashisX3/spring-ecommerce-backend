# Phase 2 — Product Module

## Overview
Full product catalog with category tree, brands, tags, product variants with JSON attributes, image management, and a dynamic filtering/search system. ~38 new files.

---

## 1. Entities & Relationships

### Category
```
Category
├── id: Long (PK, auto)
├── name: String (not blank)
├── slug: String (unique, auto-generated from name)
├── description: String (nullable)
├── imageUrl: String (nullable)
├── parent: @ManyToOne → Category (nullable, self-referencing)
├── children: @OneToMany → Category (mappedBy=parent)
├── sortOrder: int (default 0)
├── isActive: boolean (default true)
├── createdAt: Instant
├── updatedAt: Instant
```

### Brand
```
Brand
├── id: Long (PK, auto)
├── name: String (not blank)
├── slug: String (unique, auto-generated)
├── description: String (nullable)
├── logoUrl: String (nullable)
├── website: String (nullable)
├── isActive: boolean (default true)
├── createdAt: Instant
├── updatedAt: Instant
```

### Tag
```
Tag
├── id: Long (PK, auto)
├── name: String (not blank)
├── slug: String (unique, auto-generated)
├── createdAt: Instant
```

### Product
```
Product
├── id: Long (PK, auto)
├── uuid: String (unique, generated via @PrePersist)
├── sku: String (unique)
├── name: String (not blank)
├── slug: String (unique, auto-generated)
├── description: String (TEXT, nullable)
├── shortDescription: String (nullable)
├── basePrice: BigDecimal (>= 0)
├── attributes: String → stored as JSON column
├── category: @ManyToOne → Category
├── brand: @ManyToOne → Brand
├── tags: @ManyToMany → Tag
├── isActive: boolean (default true)
├── isFeatured: boolean (default false)
├── createdAt: Instant
├── updatedAt: Instant
```

### ProductVariant
```
ProductVariant
├── id: Long (PK, auto)
├── sku: String (unique)
├── name: String (not blank)
├── price: BigDecimal (nullable — null = use product.basePrice)
├── stock: int (default 0)
├── attributes: String → stored as JSON column
├── isActive: boolean (default true)
├── sortOrder: int (default 0)
├── product: @ManyToOne → Product
├── createdAt: Instant
├── updatedAt: Instant
```

### ProductImage
```
ProductImage
├── id: Long (PK, auto)
├── imageUrl: String (not blank)
├── isPrimary: boolean (default false)
├── sortOrder: int (default 0)
├── product: @ManyToOne → Product
├── variant: @ManyToOne → ProductVariant (nullable)
```

---

## 2. API Endpoints

### Category Controller — `/categories`

| Method | Path | Permission | Description |
|--------|------|------------|-------------|
| GET | `/categories` | `category:read` | Flat list with parentId |
| GET | `/categories/tree` | `category:read` | Nested tree |
| GET | `/categories/{slug}` | `category:read` | Single with children |
| POST | `/categories` | `category:write` | Create |
| PUT | `/categories/{slug}` | `category:write` | Update |
| DELETE | `/categories/{slug}` | `category:write` | Delete |

### Brand Controller — `/brands`

| Method | Path | Permission | Description |
|--------|------|------------|-------------|
| GET | `/brands` | `brand:read` | List |
| GET | `/brands/{slug}` | `brand:read` | Single |
| POST | `/brands` | `brand:write` | Create |
| PUT | `/brands/{slug}` | `brand:write` | Update |
| DELETE | `/brands/{slug}` | `brand:write` | Delete |

### Tag Controller — `/tags`

| Method | Path | Permission | Description |
|--------|------|------------|-------------|
| GET | `/tags` | `tag:read` | List |
| POST | `/tags` | `tag:write` | Create |
| DELETE | `/tags/{slug}` | `tag:write` | Delete |

### Product Controller — `/products`

| Method | Path | Permission | Description |
|--------|------|------------|-------------|
| GET | `/products` | `product:read` | Filtered + paginated list |
| GET | `/products/{uuid}` | `product:read` | Single with full details |
| GET | `/products/{uuid}/similar` | `product:read` | Similar products |
| POST | `/products` | `product:write` | Create with variants + images |
| PUT | `/products/{uuid}` | `product:write` | Update |
| DELETE | `/products/{uuid}` | `product:write` | Delete |

### Variant Controller — `/products/{uuid}/variants`

| Method | Path | Permission | Description |
|--------|------|------------|-------------|
| GET | `/products/{uuid}/variants` | `product:read` | List variants |
| POST | `/products/{uuid}/variants` | `product:write` | Add variant |
| PUT | `/variants/{id}` | `product:write` | Update variant |
| DELETE | `/variants/{id}` | `product:write` | Delete variant |

### Image Controller — `/products/{uuid}/images`

| Method | Path | Permission | Description |
|--------|------|------------|-------------|
| GET | `/products/{uuid}/images` | `product:read` | List images |
| POST | `/products/{uuid}/images` | `product:write` | Add image |
| DELETE | `/images/{id}` | `product:write` | Delete image |

---

## 3. Edge Cases & Design Decisions

### 3.1 Slug Generation
- Each entity with `name` → `slug` conversion happens in the Service layer
- Pattern: lowercase, replace spaces with hyphens, remove special chars
- **Collision handling**: If slug already exists, append `-1`, `-2`, etc. until unique
- Implemented via `findBySlug()` check + loop in service

### 3.2 Category Tree Operations
- **Delete with children**: BLOCKED. Throw `CategoryHasChildrenException` (400). Force user to reassign/reparent children first.
- **Delete with products**: BLOCKED. Throw `CategoryHasProductsException` (400). Force reassign products first.
- **Circular parent reference**: Validate on create/update. New parent must not be the category itself or any of its descendants.
- **Max depth**: No hard limit enforced, but document as a future concern.

### 3.3 Product UUID
- Same pattern as `User.uuid`: generated via `@PrePersist` with `UUID.randomUUID().toString()`
- Used in all public API URLs (never expose auto-increment `id`)
- Internal `id` used for DB relationships

### 3.4 Deletion Safety
| Operation | Behavior |
|-----------|----------|
| Delete category with children | **BLOCKED** — `CategoryHasChildrenException` |
| Delete category with products | **BLOCKED** — `CategoryHasProductsException` |
| Delete brand with products | **BLOCKED** — `BrandHasProductsException` |
| Delete tag with products | **Allowed** — ManyToMany join table auto-cleaned |
| Delete product with variants | **CASCADE** — all variants + images deleted |
| Delete product (deactivated) | **Allowed** — same as active |
| Delete last variant of product | **Allowed** — product exists without variants |
| Delete primary image | **Auto-assign** next image as primary |

### 3.5 JSON Attributes
- Stored as `@Column(columnDefinition = "JSON")` with a JPA converter (`String` ↔ `Map<String,String>`)
- **Filtering**: Use JPA `Specification` with native `JSON_EXTRACT` queries
  - Hibernate 7 supports `@JdbcTypeCode(SqlTypes.JSON)` for automatic mapping
  - Query: `JSON_EXTRACT(attributes, '$.color') = 'Black'`
- **Performance concern**: Unindexed JSON queries on large datasets.
  - **Future**: Add MySQL generated columns for frequently filtered attributes
  - **Future**: Migrate to normalized Attribute tables (Option B in design doc)

### 3.6 Price Handling
- `basePrice` on Product — required, >= 0
- `price` on Variant — nullable. If null, `getEffectivePrice()` returns `product.basePrice`
- `minPrice`/`maxPrice` in product response — computed from variant prices or basePrice
- Sorting by price uses `basePrice` for product-level sort
- Validation: `basePrice >= 0`, variant `price >= 0` if set

### 3.7 Filtering System
**Query params accepted by `GET /products`:**

| Param | Type | Example | Behavior |
|-------|------|---------|----------|
| `category` | String (slug) | `mobile-phones` | Exact match |
| `brand` | String (slug) | `apple` | Exact match |
| `tag` | String (slug) | `premium` | Exact match (repeatable: AND logic) |
| `search` | String | `iphone` | LIKE on name, slug, shortDescription |
| `minPrice` | BigDecimal | `10000` | >= basePrice |
| `maxPrice` | BigDecimal | `50000` | <= basePrice |
| `isFeatured` | Boolean | `true` | Exact match |
| `isActive` | Boolean | `true` | Exact match (default: true) |
| `{attributeKey}` | String | `color=Black` | JSON_EXTRACT match (repeatable: AND logic) |
| `sortBy` | String | `price` | price, name, createdAt (default: createdAt) |
| `sortDir` | String | `asc` | asc, desc (default: desc) |
| `page` | Integer | `0` | Zero-indexed (default: 0) |
| `size` | Integer | `20` | Max 100 (default: 20) |

**Sort-by-price note**: Sorts by `basePrice` (product level), not individual variant prices.

### 3.8 Similar Products
```
GET /products/{uuid}/similar
```
Logic:
1. Find products in same category
2. Score by overlapping brand + attribute key-value pairs
3. Exclude the product itself
4. Return top 10, sorted by similarity score DESC

**Implementation**: Service method runs a query, computes scores in Java, returns top results.

### 3.9 Data Validation
| Field | Rule |
|-------|------|
| name | `@NotBlank`, max 255 chars |
| sku | `@NotBlank`, max 100 chars, unique |
| basePrice | `@DecimalMin("0.00")` |
| stock (variant) | `@Min(0)` |
| slug | Auto-generated, unique |
| uuid | Auto-generated, unique |

---

## 4. New Permissions (DataSeeder)

| Permission | Controller Usage |
|-----------|-----------------|
| `product:read` | GET products, variants, images |
| `product:write` | POST/PUT/DELETE products, variants, images |
| `category:read` | GET categories |
| `category:write` | POST/PUT/DELETE categories |
| `brand:read` | GET brands |
| `brand:write` | POST/PUT/DELETE brands |
| `tag:read` | GET tags |
| `tag:write` | POST/DELETE tags |

---

## 5. New Exception Classes

| Exception | Status | When |
|-----------|--------|------|
| `CategoryHasChildrenException` | 400 | Delete category with children |
| `CategoryHasProductsException` | 400 | Delete category with products |
| `BrandHasProductsException` | 400 | Delete brand with products |
| `CircularCategoryReferenceException` | 400 | Setting parent to descendant |
| `DuplicateSkuException` | 409 | Duplicate SKU on product or variant |
| `CategoryNotFoundException` | 404 | Category slug not found |
| `BrandNotFoundException` | 404 | Brand slug not found |
| `TagNotFoundException` | 404 | Tag slug not found |
| `ProductNotFoundException` | 404 | Product UUID not found |
| `ProductVariantNotFoundException` | 404 | Variant ID not found |

---

## 6. Implementation Steps

### Step A — Category (~12 files)
1. `Category.java` entity
2. `CategoryRepository.java`
3. `CategoryRequest.java`, `CategoryResponse.java`
4. `CategoryMapper.java`
5. `CategoryService.java` (CRUD + tree + circular ref validation + delete guard)
6. `CategoryController.java` (flat + tree endpoints)
7. Exceptions: `CategoryHasChildrenException`, `CategoryHasProductsException`, `CircularCategoryReferenceException`, `CategoryNotFoundException`

### Step B — Brand (~8 files)
1. `Brand.java` entity
2. `BrandRepository.java`
3. `BrandRequest.java`, `BrandResponse.java`
4. `BrandMapper.java`
5. `BrandService.java` (CRUD + delete guard)
6. `BrandController.java`
7. Exception: `BrandHasProductsException`, `BrandNotFoundException`

### Step C — Tag (~6 files)
1. `Tag.java` entity
2. `TagRepository.java`
3. `TagRequest.java`, `TagResponse.java`
4. `TagMapper.java`
5. `TagService.java` (CRUD)
6. `TagController.java`
7. Exception: `TagNotFoundException`

### Step D — Product (~10 files)
1. `Product.java` entity (with JSON converter)
2. `ProductRepository.java` (with JPA Specifications)
3. `ProductRequest.java`, `ProductResponse.java` (with nested variants/images)
4. `ProductMapper.java`
5. `ProductService.java` (CRUD + filter + similar)
6. `ProductSpecification.java` (dynamic query builder)
7. `ProductController.java`
8. Exception: `DuplicateSkuException`, `ProductNotFoundException`

### Step E — Variants + Images (~6 files)
1. `ProductVariant.java`, `ProductImage.java` entities
2. `ProductVariantRepository.java`, `ProductImageRepository.java`
3. Integrated into `ProductService.java` (no separate service needed)
4. `VariantController.java`, `ImageController.java`
5. Exception: `ProductVariantNotFoundException`

### Step F — Update DataSeeder (~1 file)
- Add 8 new permissions
- Optional: seed default category/brand if desired

---

## 7. Future Concerns

### Performance
- **JSON attribute queries** will get slow beyond ~10K products. Mitigation:
  - Short-term: MySQL generated columns + indexes on frequently filtered attributes
  - Long-term: Normalized `AttributeDefinition` + `AttributeValue` tables
- **Category tree** — fetching entire tree with many categories. Mitigation:
  - Add `path` materialized column (`/electronics/mobile/apple`) for faster subtree queries
  - Client-side caching of tree (rarely changes)

### Scalability
- **Filtering** — current JPA Specifications work for moderate loads. For high traffic:
  - Add Redis caching for common filter combinations
  - Consider Elasticsearch for full-text + faceted search
- **Similar products** — current in-memory scoring is O(n * attributes). Mitigation:
  - Pre-compute similarity scores on product save
  - Store in a separate `product_similarity` table

### Product Lifecycle
- **Scheduling** — future `availableFrom` / `availableTo` dates for timed releases
- **Bulk import** — future CSV/JSON import endpoint
- **Draft/published** — future state machine beyond `isActive`
- **Versioning** — future need to track product changes

### Multi-tenancy / Multi-store
- For Phase 1, ignore. Future: add `store_id` to Product, Category, Brand.
- Filter all queries by store context.

### Internationalization
- `name`, `description`, `shortDescription` — future need for locale-specific translations
- Approach: `ProductTranslation(product_id, locale, name, description)` table

---

## 8. File Summary

```
modules/product/
├── entity/
│   ├── Category.java
│   ├── Brand.java
│   ├── Tag.java
│   ├── Product.java
│   ├── ProductVariant.java
│   └── ProductImage.java
├── repository/
│   ├── CategoryRepository.java
│   ├── BrandRepository.java
│   ├── TagRepository.java
│   ├── ProductRepository.java
│   ├── ProductVariantRepository.java
│   └── ProductImageRepository.java
├── service/
│   ├── CategoryService.java
│   ├── BrandService.java
│   ├── TagService.java
│   └── ProductService.java
├── controller/
│   ├── CategoryController.java
│   ├── BrandController.java
│   ├── TagController.java
│   ├── ProductController.java
│   ├── VariantController.java
│   └── ImageController.java
├── dto/request/
│   ├── CategoryRequest.java
│   ├── BrandRequest.java
│   ├── TagRequest.java
│   ├── ProductRequest.java
│   ├── VariantRequest.java
│   └── ImageRequest.java
├── dto/response/
│   ├── CategoryResponse.java
│   ├── BrandResponse.java
│   ├── TagResponse.java
│   ├── ProductResponse.java
│   ├── VariantResponse.java
│   └── ImageResponse.java
├── mapper/
│   ├── CategoryMapper.java
│   ├── BrandMapper.java
│   ├── TagMapper.java
│   ├── ProductMapper.java
│   └── VariantMapper.java (includes image mapping)
├── specification/
│   └── ProductSpecification.java
└── exception/ (10 exceptions)

+ DataSeeder.java (modified)
```

**Total: ~39 new files + 1 modified**
