# Database Schema

## Conventions

- All tables use `BIGINT` auto-increment `id` as PK
- All core entities have `uuid VARCHAR(36) UNIQUE NOT NULL` (generated via `@PrePersist`)
- Monetary columns use `DECIMAL(19,4)` to match Wallet precision
- Timestamps use `TIMESTAMP` mapped to `java.time.Instant`
- FK column names use `snake_case` with `_id` suffix

## Auth / Security

### users
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| first_name | VARCHAR(255) | NOT NULL |
| last_name | VARCHAR(255) | NOT NULL |
| email | VARCHAR(255) | UNIQUE NOT NULL |
| dial_code | VARCHAR(10) | NOT NULL |
| phone_number | VARCHAR(20) | UNIQUE NOT NULL |
| password | VARCHAR(255) | NOT NULL |
| profile_picture_url | VARCHAR(500) | |
| street_address | VARCHAR(500) | (embedded UserAddress) |
| city | VARCHAR(100) | (embedded UserAddress) |
| state | VARCHAR(100) | (embedded UserAddress) |
| country | VARCHAR(100) | (embedded UserAddress) |
| zip_code | VARCHAR(20) | (embedded UserAddress) |
| role_id | BIGINT | FK → roles |
| is_active | BOOLEAN | NOT NULL DEFAULT TRUE |
| is_email_verified | BOOLEAN | NOT NULL DEFAULT FALSE |
| is_phone_verified | BOOLEAN | NOT NULL DEFAULT FALSE |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### roles
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| role_name | VARCHAR(50) | UNIQUE NOT NULL |
| role_description | VARCHAR(255) | |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### permissions
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| permission_name | VARCHAR(100) | UNIQUE NOT NULL |
| permission_description | VARCHAR(255) | |
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
| effect | VARCHAR(10) | NOT NULL (GRANT / DENY) |
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

## Catalog

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
| sort_order | INT | NOT NULL DEFAULT 0 |
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
| website | VARCHAR(255) | |
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

### product_tags
| Column | Type | Constraints |
|--------|------|-------------|
| product_id | BIGINT | PK, FK → products |
| tag_id | BIGINT | PK, FK → tags |

### products
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| name | VARCHAR(255) | NOT NULL |
| slug | VARCHAR(300) | UNIQUE NOT NULL |
| short_description | VARCHAR(500) | |
| description | TEXT | |
| sku | VARCHAR(100) | UNIQUE NOT NULL |
| base_price | DECIMAL(19,4) | NOT NULL |
| attributes | JSON | |
| is_active | BOOLEAN | NOT NULL DEFAULT TRUE |
| is_featured | BOOLEAN | NOT NULL DEFAULT FALSE |
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
| price | DECIMAL(19,4) | |
| stock | INT | NOT NULL DEFAULT 0 |
| attributes | JSON | |
| sort_order | INT | NOT NULL DEFAULT 0 |
| is_default | BOOLEAN | NOT NULL DEFAULT FALSE |
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
| image_url | VARCHAR(500) | NOT NULL |
| alt_text | VARCHAR(255) | |
| sort_order | INT | NOT NULL DEFAULT 0 |
| is_primary | BOOLEAN | NOT NULL DEFAULT FALSE |
| created_at | TIMESTAMP | NOT NULL |

## Reviews & Ratings

### reviews
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| product_id | BIGINT | FK → products NOT NULL |
| user_id | BIGINT | FK → users NOT NULL |
| rating | INT | NOT NULL (1-5) |
| title | VARCHAR(255) | |
| comment | TEXT | |
| is_verified_purchase | BOOLEAN | NOT NULL DEFAULT FALSE |
| is_active | BOOLEAN | NOT NULL DEFAULT TRUE |
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

## Cart & Wishlist

### cart_items
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| user_id | BIGINT | FK → users NOT NULL |
| product_id | BIGINT | FK → products NOT NULL |
| variant_id | BIGINT | (scalar FK → product_variants, nullable) |
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

## Orders

### order_statuses (lookup table)
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| code | VARCHAR(30) | UNIQUE NOT NULL (PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, RETURN_REQUESTED, REFUNDED) |
| name | VARCHAR(50) | NOT NULL |
| description | VARCHAR(255) | |

### order_status_transitions (allowed transitions per role)
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| from_status_id | BIGINT | FK → order_statuses NOT NULL |
| to_status_id | BIGINT | FK → order_statuses NOT NULL |
| allowed_by_id | BIGINT | FK → roles NOT NULL |

### orders
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| user_id | BIGINT | FK → users NOT NULL |
| order_number | VARCHAR(50) | UNIQUE NOT NULL |
| status_id | BIGINT | FK → order_statuses |
| subtotal | DECIMAL(19,4) | NOT NULL |
| discount | DECIMAL(19,4) | NOT NULL DEFAULT 0.00 |
| shipping_cost | DECIMAL(19,4) | NOT NULL DEFAULT 0.00 |
| tax | DECIMAL(19,4) | NOT NULL DEFAULT 0.00 |
| total | DECIMAL(19,4) | NOT NULL |
| currency_id | BIGINT | FK → currencies NOT NULL |
| coupon_id | BIGINT | (scalar FK → coupons) |
| coupon_code | VARCHAR(50) | |
| notes | TEXT | |
| version | BIGINT | (optimistic lock) |
| canceled_at | TIMESTAMP | |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### order_items
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| order_id | BIGINT | FK → orders NOT NULL |
| product_id | BIGINT | (scalar FK → products) NOT NULL |
| variant_id | BIGINT | (scalar FK → product_variants) |
| product_name | VARCHAR(255) | NOT NULL |
| variant_name | VARCHAR(255) | |
| sku | VARCHAR(100) | NOT NULL |
| quantity | INT | NOT NULL |
| unit_price | DECIMAL(19,4) | NOT NULL |
| total_price | DECIMAL(19,4) | NOT NULL |
| created_at | TIMESTAMP | NOT NULL |

### order_status_history
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| order_id | BIGINT | FK → orders NOT NULL |
| from_status_id | BIGINT | FK → order_statuses |
| to_status_id | BIGINT | FK → order_statuses NOT NULL |
| changed_by | VARCHAR(100) | NOT NULL |
| reason | TEXT | |
| created_at | TIMESTAMP | NOT NULL |

## Shipping

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

### delivery_statuses (lookup table)
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| code | VARCHAR(30) | UNIQUE NOT NULL (PENDING, PROCESSING, SHIPPED, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED, FAILED, RETURNED) |
| name | VARCHAR(50) | NOT NULL |

### shipping_carriers (lookup table)
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| code | VARCHAR(30) | UNIQUE NOT NULL |
| name | VARCHAR(100) | NOT NULL |
| tracking_url_template | VARCHAR(500) | |

### deliveries
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| order_id | BIGINT | (scalar FK → orders) NOT NULL |
| shipping_address_id | BIGINT | FK → shipping_addresses NOT NULL |
| carrier_id | BIGINT | FK → shipping_carriers |
| tracking_number | VARCHAR(100) | |
| status_id | BIGINT | FK → delivery_statuses NOT NULL DEFAULT 1 |
| estimated_delivery | DATE | |
| shipped_at | TIMESTAMP | |
| delivered_at | TIMESTAMP | |
| notes | TEXT | |
| version | BIGINT | (optimistic lock) |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

## Payment

### payment_gateways (lookup table)
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| code | VARCHAR(30) | UNIQUE NOT NULL |
| name | VARCHAR(100) | NOT NULL |

### payment_statuses (lookup table)
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| code | VARCHAR(30) | UNIQUE NOT NULL (PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED, PARTIALLY_REFUNDED) |
| name | VARCHAR(50) | NOT NULL |

### payments
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| order_id | BIGINT | (scalar FK → orders) NOT NULL |
| user_id | BIGINT | FK → users NOT NULL |
| gateway_id | BIGINT | FK → payment_gateways |
| gateway_transaction_id | VARCHAR(255) | |
| amount | DECIMAL(19,4) | NOT NULL |
| currency_id | BIGINT | FK → currencies NOT NULL |
| status_id | BIGINT | FK → payment_statuses |
| method | VARCHAR(50) | |
| paid_at | TIMESTAMP | |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### refund_statuses (lookup table)
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| code | VARCHAR(30) | UNIQUE NOT NULL (PENDING, APPROVED, PROCESSING, COMPLETED, FAILED, REJECTED) |
| name | VARCHAR(50) | NOT NULL |

### refunds
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| payment_id | BIGINT | FK → payments NOT NULL |
| return_request_id | BIGINT | (scalar FK → return_requests) |
| amount | DECIMAL(19,4) | NOT NULL |
| reason | TEXT | |
| status_id | BIGINT | FK → refund_statuses |
| gateway_refund_id | VARCHAR(255) | |
| refunded_at | TIMESTAMP | |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

## Returns

### return_statuses (lookup table)
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| code | VARCHAR(30) | UNIQUE NOT NULL (PENDING, APPROVED, REJECTED, ITEM_RECEIVED, REFUND_PENDING, REFUNDED, CLOSED) |
| name | VARCHAR(50) | NOT NULL |

### return_types (lookup table)
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| code | VARCHAR(30) | UNIQUE NOT NULL (REFUND, EXCHANGE, STORE_CREDIT) |
| name | VARCHAR(50) | NOT NULL |

### return_conditions (lookup table)
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| code | VARCHAR(30) | UNIQUE NOT NULL (NEW, OPENED, DAMAGED, DEFECTIVE) |
| name | VARCHAR(50) | NOT NULL |

### return_requests
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| order_id | BIGINT | (scalar FK → orders) NOT NULL |
| user_id | BIGINT | FK → users NOT NULL |
| return_type_id | BIGINT | FK → return_types |
| reason | TEXT | NOT NULL |
| status_id | BIGINT | FK → return_statuses |
| refund_amount | DECIMAL(19,4) | |
| resolution_notes | TEXT | |
| notes | TEXT | |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### return_items
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| return_request_id | BIGINT | FK → return_requests NOT NULL |
| order_item_id | BIGINT | (scalar FK → order_items) NOT NULL |
| product_id | BIGINT | (scalar FK → products) NOT NULL |
| product_name | VARCHAR(255) | |
| sku | VARCHAR(100) | |
| quantity | INT | NOT NULL |
| unit_price | DECIMAL(19,4) | NOT NULL |
| condition_id | BIGINT | FK → return_conditions |
| condition_note | TEXT | |
| reason_detail | TEXT | |
| refund_amount | DECIMAL(19,4) | |
| created_at | TIMESTAMP | NOT NULL |

## Wallet

### wallet_transaction_types (lookup table)
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| code | VARCHAR(30) | UNIQUE NOT NULL (CREDIT, DEBIT, REFUND, PAYMENT, STORE_CREDIT) |
| name | VARCHAR(50) | NOT NULL |

### wallets
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| user_id | BIGINT | FK → users UNIQUE NOT NULL |
| balance | DECIMAL(19,4) | NOT NULL DEFAULT 0.00 |
| currency_id | BIGINT | FK → currencies NOT NULL |
| is_active | BOOLEAN | NOT NULL DEFAULT TRUE |
| version | BIGINT | (optimistic lock) |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### wallet_transactions
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| wallet_id | BIGINT | FK → wallets NOT NULL |
| type_id | BIGINT | FK → wallet_transaction_types |
| amount | DECIMAL(19,4) | NOT NULL |
| balance_before | DECIMAL(19,4) | NOT NULL |
| balance_after | DECIMAL(19,4) | NOT NULL |
| reference_type | VARCHAR(50) | (e.g. "REFUND", "PAYMENT", "STORE_CREDIT") |
| reference_id | BIGINT | |
| description | VARCHAR(255) | |
| created_at | TIMESTAMP | NOT NULL |

## Currency

### currencies (lookup table)
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| code | VARCHAR(3) | UNIQUE NOT NULL (USD, EUR, GBP, etc.) |
| name | VARCHAR(50) | NOT NULL |
| symbol | VARCHAR(5) | |
| exchange_rate | DECIMAL(19,6) | NOT NULL DEFAULT 1.000000 |

## Coupons, Discounts & Offers

### discount_types (lookup table)
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| code | VARCHAR(30) | UNIQUE NOT NULL (PERCENTAGE, FIXED_AMOUNT) |
| name | VARCHAR(50) | NOT NULL |

### coupons
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| code | VARCHAR(50) | UNIQUE NOT NULL |
| description | TEXT | |
| discount_type_id | BIGINT | FK → discount_types NOT NULL |
| discount_value | DECIMAL(19,4) | NOT NULL |
| min_order_amount | DECIMAL(19,4) | |
| max_discount | DECIMAL(19,4) | (for percentage coupons) |
| usage_limit | INT | |
| usage_limit_per_user | INT | |
| total_used | INT | NOT NULL DEFAULT 0 |
| is_global | BOOLEAN | NOT NULL DEFAULT FALSE |
| is_active | BOOLEAN | NOT NULL DEFAULT TRUE |
| valid_from | TIMESTAMP | NOT NULL |
| valid_until | TIMESTAMP | NOT NULL |
| version | BIGINT | (optimistic lock) |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### coupon_usage
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| coupon_id | BIGINT | (scalar FK → coupons) NOT NULL |
| user_id | BIGINT | (scalar FK → users) NOT NULL |
| order_id | BIGINT | (scalar FK → orders) |
| discount_amount | DECIMAL(19,4) | NOT NULL |
| used_at | TIMESTAMP | NOT NULL |

### coupon_assignments
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| coupon_id | BIGINT | FK → coupons NOT NULL |
| user_id | BIGINT | FK → users NOT NULL |
| assigned_at | TIMESTAMP | NOT NULL |

### discounts
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| name | VARCHAR(100) | NOT NULL |
| description | TEXT | |
| discount_type_id | BIGINT | FK → discount_types NOT NULL |
| discount_value | DECIMAL(19,4) | NOT NULL |
| is_active | BOOLEAN | NOT NULL DEFAULT TRUE |
| valid_from | TIMESTAMP | NOT NULL |
| valid_until | TIMESTAMP | NOT NULL |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### discount_assignments
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| discount_id | BIGINT | FK → discounts NOT NULL |
| user_id | BIGINT | FK → users NOT NULL |
| assigned_at | TIMESTAMP | NOT NULL |

### offers
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| name | VARCHAR(100) | NOT NULL |
| description | TEXT | |
| discount_type_id | BIGINT | FK → discount_types NOT NULL |
| discount_value | DECIMAL(19,4) | NOT NULL |
| is_active | BOOLEAN | NOT NULL DEFAULT TRUE |
| valid_from | TIMESTAMP | NOT NULL |
| valid_until | TIMESTAMP | NOT NULL |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### offer_usage
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| offer_id | BIGINT | (scalar FK → offers) NOT NULL |
| user_id | BIGINT | FK → users NOT NULL |
| discount_amount | DECIMAL(19,4) | NOT NULL |
| used_at | TIMESTAMP | NOT NULL |

### offer_assignments
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| offer_id | BIGINT | FK → offers NOT NULL |
| user_id | BIGINT | FK → users NOT NULL |
| assigned_at | TIMESTAMP | NOT NULL |

## Notifications

### notifications
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| user_id | BIGINT | FK → users NOT NULL |
| type | VARCHAR(50) | NOT NULL (ORDER_CONFIRMED, ORDER_STATUS_CHANGED, PAYMENT_RECEIVED, PAYMENT_FAILED, DELIVERY_UPDATED, CHAT_MESSAGE, ADMIN_BROADCAST) |
| title | VARCHAR(200) | NOT NULL |
| body | TEXT | |
| deep_link | VARCHAR(500) | (e.g. "ecommerce://orders/{uuid}" or "https://app.example.com/orders/{uuid}") |
| is_read | BOOLEAN | NOT NULL DEFAULT FALSE |
| created_at | TIMESTAMP | NOT NULL |
| read_at | TIMESTAMP | |

## Chat / Support

### chat_rooms
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| user_id | BIGINT | FK → users NOT NULL |
| agent_id | BIGINT | FK → users |
| status | VARCHAR(20) | NOT NULL DEFAULT 'BOT_ACTIVE' (BOT_ACTIVE, AWAITING_AGENT, ACTIVE, CLOSED) |
| topic | VARCHAR(100) | (derived from bot Q&A: "ORDER_ISSUE", "PAYMENT_ISSUE", etc.) |
| created_at | TIMESTAMP | NOT NULL |
| assigned_at | TIMESTAMP | |
| closed_at | TIMESTAMP | |

### chat_messages
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| uuid | VARCHAR(36) | UNIQUE NOT NULL |
| room_id | BIGINT | FK → chat_rooms NOT NULL |
| sender_type | VARCHAR(10) | NOT NULL (BOT, USER, AGENT, SYSTEM) |
| sender_id | BIGINT | FK → users (nullable for BOT/SYSTEM) |
| content | TEXT | NOT NULL |
| message_type | VARCHAR(20) | NOT NULL DEFAULT 'TEXT' (TEXT, QUICK_REPLY, OPTION_SELECTED, TYPING) |
| metadata | JSON | (e.g. {"options": [{"label": "Order Issue", "value": "order_issue"}]}) |
| created_at | TIMESTAMP | NOT NULL |
| read_at | TIMESTAMP | |

### chat_bot_questions
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PK AUTO_INCREMENT |
| parent_id | BIGINT | FK → chat_bot_questions (self) |
| question_key | VARCHAR(100) | UNIQUE NOT NULL |
| question_text | VARCHAR(500) | NOT NULL |
| options | JSON | (e.g. [{"label": "Order Issue", "value": "order_issue", "nextQuestionKey": "order_detail"}]) |
| bot_response | TEXT | |
| is_escalation_point | BOOLEAN | NOT NULL DEFAULT FALSE |
| sort_order | INT | NOT NULL DEFAULT 0 |
| is_active | BOOLEAN | NOT NULL DEFAULT TRUE |
