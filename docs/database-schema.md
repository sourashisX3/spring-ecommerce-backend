# Database Schema

## Existing Tables

### users
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| email | VARCHAR(255) | UNIQUE NOT NULL |
| password | VARCHAR(255) | NOT NULL |
| first_name | VARCHAR(100) | NOT NULL |
| last_name | VARCHAR(100) | NOT NULL |
| phone | VARCHAR(20) | |
| avatar_url | VARCHAR(500) | |
| is_active | BOOLEAN | NOT NULL DEFAULT TRUE |
| is_email_verified | BOOLEAN | NOT NULL DEFAULT FALSE |
| role_id | BIGINT | FK → roles |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### roles
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| name | VARCHAR(50) | UNIQUE NOT NULL |
| description | VARCHAR(255) | |
| is_system | BOOLEAN | NOT NULL DEFAULT FALSE |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### permissions
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| code | VARCHAR(100) | UNIQUE NOT NULL |
| description | VARCHAR(255) | |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### role_permissions
| Column | Type | Constraints |
|--------|------|-------------|
| role_id | BIGINT | PK, FK → roles |
| permission_id | BIGINT | PK, FK → permissions |

### user_permissions
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| user_id | BIGINT | FK → users NOT NULL |
| permission_id | BIGINT | FK → permissions NOT NULL |
| granted | BOOLEAN | NOT NULL DEFAULT TRUE |
| created_at | TIMESTAMP | NOT NULL |

### categories
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| name | VARCHAR(100) | NOT NULL |
| slug | VARCHAR(150) | UNIQUE NOT NULL |
| description | TEXT | |
| image_url | VARCHAR(500) | |
| is_active | BOOLEAN | NOT NULL DEFAULT TRUE |
| parent_id | BIGINT | FK → categories (self) |
| display_order | INT | NOT NULL DEFAULT 0 |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### brands
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| name | VARCHAR(100) | NOT NULL |
| slug | VARCHAR(150) | UNIQUE NOT NULL |
| description | TEXT | |
| logo_url | VARCHAR(500) | |
| is_active | BOOLEAN | NOT NULL DEFAULT TRUE |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### tags
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| name | VARCHAR(50) | NOT NULL |
| slug | VARCHAR(100) | UNIQUE NOT NULL |
| is_active | BOOLEAN | NOT NULL DEFAULT TRUE |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### products
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| name | VARCHAR(255) | NOT NULL |
| slug | VARCHAR(300) | UNIQUE NOT NULL |
| description | TEXT | |
| base_price | DECIMAL(10,2) | NOT NULL |
| is_active | BOOLEAN | NOT NULL DEFAULT TRUE |
| category_id | BIGINT | FK → categories NOT NULL |
| brand_id | BIGINT | FK → brands |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### product_variants
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| product_id | BIGINT | FK → products NOT NULL |
| sku | VARCHAR(100) | UNIQUE NOT NULL |
| name | VARCHAR(255) | NOT NULL |
| price | DECIMAL(10,2) | |
| stock | INT | NOT NULL DEFAULT 0 |
| is_active | BOOLEAN | NOT NULL DEFAULT TRUE |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### product_images
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| product_id | BIGINT | FK → products NOT NULL |
| variant_id | BIGINT | FK → product_variants |
| url | VARCHAR(500) | NOT NULL |
| alt_text | VARCHAR(255) | |
| display_order | INT | NOT NULL DEFAULT 0 |
| is_primary | BOOLEAN | NOT NULL DEFAULT FALSE |
| created_at | TIMESTAMP | NOT NULL |

### reviews
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| product_id | BIGINT | FK → products NOT NULL |
| user_id | BIGINT | FK → users NOT NULL |
| rating | INT | NOT NULL (1-5) |
| title | VARCHAR(255) | |
| content | TEXT | |
| is_verified_purchase | BOOLEAN | NOT NULL DEFAULT FALSE |
| is_approved | BOOLEAN | NOT NULL DEFAULT FALSE |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### review_votes
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| review_id | BIGINT | FK → reviews NOT NULL |
| user_id | BIGINT | FK → users NOT NULL |
| vote_type | VARCHAR(10) | NOT NULL (UPVOTE / DOWNVOTE) |
| created_at | TIMESTAMP | NOT NULL |

### cart_items
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| user_id | BIGINT | FK → users NOT NULL |
| product_id | BIGINT | FK → products NOT NULL |
| variant_id | BIGINT | FK → product_variants |
| quantity | INT | NOT NULL DEFAULT 1 |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### wishlist_items
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| user_id | BIGINT | FK → users NOT NULL |
| product_id | BIGINT | FK → products NOT NULL |
| created_at | TIMESTAMP | NOT NULL |

### otp
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| email | VARCHAR(255) | NOT NULL |
| code | VARCHAR(6) | NOT NULL |
| purpose | VARCHAR(50) | NOT NULL |
| expires_at | TIMESTAMP | NOT NULL |
| used | BOOLEAN | NOT NULL DEFAULT FALSE |
| created_at | TIMESTAMP | NOT NULL |

### refresh_tokens
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| user_id | BIGINT | FK → users NOT NULL |
| token | VARCHAR(500) | UNIQUE NOT NULL |
| expires_at | TIMESTAMP | NOT NULL |
| revoked | BOOLEAN | NOT NULL DEFAULT FALSE |
| created_at | TIMESTAMP | NOT NULL |

---

## New Tables

### orders
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| user_id | BIGINT | FK → users NOT NULL |
| order_number | VARCHAR(50) | UNIQUE NOT NULL |
| status | VARCHAR(30) | NOT NULL DEFAULT 'PENDING' |
| subtotal | DECIMAL(10,2) | NOT NULL |
| discount | DECIMAL(10,2) | NOT NULL DEFAULT 0.00 |
| shipping_cost | DECIMAL(10,2) | NOT NULL DEFAULT 0.00 |
| tax | DECIMAL(10,2) | NOT NULL DEFAULT 0.00 |
| total | DECIMAL(10,2) | NOT NULL |
| currency | VARCHAR(3) | NOT NULL DEFAULT 'USD' |
| coupon_id | BIGINT | FK → coupons |
| coupon_code | VARCHAR(50) | |
| notes | TEXT | |
| canceled_at | TIMESTAMP | |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### order_items
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| order_id | BIGINT | FK → orders NOT NULL |
| product_id | BIGINT | FK → products NOT NULL |
| variant_id | BIGINT | FK → product_variants |
| product_name | VARCHAR(255) | NOT NULL |
| variant_name | VARCHAR(255) | |
| sku | VARCHAR(100) | NOT NULL |
| quantity | INT | NOT NULL |
| unit_price | DECIMAL(10,2) | NOT NULL |
| total_price | DECIMAL(10,2) | NOT NULL |
| created_at | TIMESTAMP | NOT NULL |

### order_status_history
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| order_id | BIGINT | FK → orders NOT NULL |
| from_status | VARCHAR(30) | |
| to_status | VARCHAR(30) | NOT NULL |
| changed_by | VARCHAR(100) | NOT NULL |
| reason | TEXT | |
| created_at | TIMESTAMP | NOT NULL |

### shipping_addresses
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| user_id | BIGINT | FK → users NOT NULL |
| label | VARCHAR(100) | (e.g. "Home", "Office") |
| recipient_name | VARCHAR(255) | NOT NULL |
| phone | VARCHAR(20) | NOT NULL |
| address_line1 | VARCHAR(255) | NOT NULL |
| address_line2 | VARCHAR(255) | |
| city | VARCHAR(100) | NOT NULL |
| state | VARCHAR(100) | NOT NULL |
| postal_code | VARCHAR(20) | NOT NULL |
| country | VARCHAR(100) | NOT NULL |
| is_default | BOOLEAN | NOT NULL DEFAULT FALSE |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### deliveries
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| order_id | BIGINT | FK → orders NOT NULL |
| shipping_address_id | BIGINT | FK → shipping_addresses NOT NULL |
| carrier | VARCHAR(100) | |
| tracking_number | VARCHAR(100) | |
| status | VARCHAR(30) | NOT NULL DEFAULT 'PENDING' |
| estimated_delivery | DATE | |
| shipped_at | TIMESTAMP | |
| delivered_at | TIMESTAMP | |
| notes | TEXT | |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### wallets
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| user_id | BIGINT | FK → users UNIQUE NOT NULL |
| balance | DECIMAL(10,2) | NOT NULL DEFAULT 0.00 |
| currency | VARCHAR(3) | NOT NULL DEFAULT 'USD' |
| is_active | BOOLEAN | NOT NULL DEFAULT TRUE |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### wallet_transactions
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| wallet_id | BIGINT | FK → wallets NOT NULL |
| type | VARCHAR(30) | NOT NULL (CREDIT / DEBIT) |
| amount | DECIMAL(10,2) | NOT NULL |
| balance_before | DECIMAL(10,2) | NOT NULL |
| balance_after | DECIMAL(10,2) | NOT NULL |
| reference_type | VARCHAR(50) | (e.g. "REFUND", "PAYMENT", "STORE_CREDIT") |
| reference_id | BIGINT | |
| description | VARCHAR(255) | |
| created_at | TIMESTAMP | NOT NULL |

### coupons
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| code | VARCHAR(50) | UNIQUE NOT NULL |
| description | TEXT | |
| discount_type | VARCHAR(20) | NOT NULL (PERCENTAGE / FIXED_AMOUNT) |
| discount_value | DECIMAL(10,2) | NOT NULL |
| min_order_amount | DECIMAL(10,2) | |
| max_discount | DECIMAL(10,2) | (for percentage coupons) |
| usage_limit | INT | |
| usage_limit_per_user | INT | |
| total_used | INT | NOT NULL DEFAULT 0 |
| is_active | BOOLEAN | NOT NULL DEFAULT TRUE |
| valid_from | TIMESTAMP | NOT NULL |
| valid_until | TIMESTAMP | NOT NULL |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### coupon_usage
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| coupon_id | BIGINT | FK → coupons NOT NULL |
| user_id | BIGINT | FK → users NOT NULL |
| order_id | BIGINT | FK → orders |
| discount_amount | DECIMAL(10,2) | NOT NULL |
| used_at | TIMESTAMP | NOT NULL |

### return_requests
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| order_id | BIGINT | FK → orders NOT NULL |
| user_id | BIGINT | FK → users NOT NULL |
| reason | TEXT | NOT NULL |
| type | VARCHAR(20) | NOT NULL (REFUND / EXCHANGE / STORE_CREDIT) |
| status | VARCHAR(20) | NOT NULL DEFAULT 'PENDING' |
| notes | TEXT | |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### return_items
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| return_request_id | BIGINT | FK → return_requests NOT NULL |
| order_item_id | BIGINT | FK → order_items NOT NULL |
| product_id | BIGINT | FK → products NOT NULL |
| variant_id | BIGINT | FK → product_variants |
| quantity | INT | NOT NULL |
| unit_price | DECIMAL(10,2) | NOT NULL |
| reason_detail | TEXT | |
| condition | VARCHAR(20) | (NEW / OPENED / DAMAGED / DEFECTIVE) |
| refund_amount | DECIMAL(10,2) | |
| created_at | TIMESTAMP | NOT NULL |

### payments
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| order_id | BIGINT | FK → orders NOT NULL |
| user_id | BIGINT | FK → users NOT NULL |
| gateway | VARCHAR(50) | NOT NULL (e.g. "MOCK", "STRIPE") |
| gateway_transaction_id | VARCHAR(255) | |
| amount | DECIMAL(10,2) | NOT NULL |
| currency | VARCHAR(3) | NOT NULL DEFAULT 'USD' |
| status | VARCHAR(20) | NOT NULL DEFAULT 'PENDING' |
| method | VARCHAR(50) | |
| paid_at | TIMESTAMP | |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### refunds
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| payment_id | BIGINT | FK → payments NOT NULL |
| return_request_id | BIGINT | FK → return_requests |
| amount | DECIMAL(10,2) | NOT NULL |
| reason | TEXT | |
| status | VARCHAR(20) | NOT NULL DEFAULT 'PENDING' |
| gateway_refund_id | VARCHAR(255) | |
| refunded_at | TIMESTAMP | |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |
