# API Overview

## Auth - `/api/auth`
| Method | Path | Description | Auth |
|--------|------|-------------|------|
| POST | `/api/auth/register` | Register new user | No |
| POST | `/api/auth/login` | Login (returns JWT) | No |
| POST | `/api/auth/refresh` | Refresh JWT | No |
| POST | `/api/auth/logout` | Logout (revoke refresh token) | Yes |

## OTP - `/api/otp`
| Method | Path | Description | Auth |
|--------|------|-------------|------|
| POST | `/api/otp/send` | Send OTP email | No |
| POST | `/api/otp/verify` | Verify OTP code | No |

## Users - `/api/users`
| Method | Path | Description | Auth |
|--------|------|-------------|------|
| GET | `/api/users/me` | Get current user profile | Yes |
| PUT | `/api/users/me` | Update profile | Yes |
| PUT | `/api/users/me/password` | Change password | Yes |
| GET | `/api/users/me/permissions` | Get effective permissions | Yes |
| PUT | `/api/users/{id}/deactivate` | Deactivate user | Admin |
| PUT | `/api/users/{id}/activate` | Activate user | Admin |
| DELETE | `/api/users/{id}` | Delete user | Admin |

## Roles - `/api/roles`
| Method | Path | Description | Auth |
|--------|------|-------------|------|
| GET | `/api/roles` | List roles | Admin |
| POST | `/api/roles` | Create role | Admin |
| PUT | `/api/roles/{id}` | Update role | Admin |
| DELETE | `/api/roles/{id}` | Delete role | Admin |

## Permissions - `/api/permissions`
| Method | Path | Description | Auth |
|--------|------|-------------|------|
| GET | `/api/permissions` | List permissions | Admin |
| POST | `/api/permissions` | Create permission | Admin |
| PUT | `/api/permissions/{id}` | Update permission | Admin |
| DELETE | `/api/permissions/{id}` | Delete permission | Admin |

## Categories - `/api/categories`
| Method | Path | Description | Auth |
|--------|------|-------------|------|
| GET | `/api/categories` | List (tree) | No |
| GET | `/api/categories/{id}` | Get by ID | No |
| POST | `/api/categories` | Create | Admin |
| PUT | `/api/categories/{id}` | Update | Admin |
| PATCH | `/api/categories/{id}/status` | Toggle active | Admin |
| DELETE | `/api/categories/{id}` | Delete | Admin |

## Brands - `/api/brands`
| Method | Path | Description | Auth |
|--------|------|-------------|------|
| GET | `/api/brands` | List | No |
| GET | `/api/brands/{id}` | Get by ID | No |
| POST | `/api/brands` | Create | Admin |
| PUT | `/api/brands/{id}` | Update | Admin |
| PATCH | `/api/brands/{id}/status` | Toggle active | Admin |
| DELETE | `/api/brands/{id}` | Delete | Admin |

## Tags - `/api/tags`
| Method | Path | Description | Auth |
|--------|------|-------------|------|
| GET | `/api/tags` | List | No |
| GET | `/api/tags/{id}` | Get by ID | No |
| POST | `/api/tags` | Create | Admin |
| PUT | `/api/tags/{id}` | Update | Admin |
| PATCH | `/api/tags/{id}/status` | Toggle active | Admin |
| DELETE | `/api/tags/{id}` | Delete | Admin |

## Products - `/api/products`
| Method | Path | Description | Auth |
|--------|------|-------------|------|
| GET | `/api/products` | List (filtered, paginated) | No |
| GET | `/api/products/{id}` | Get by ID | No |
| POST | `/api/products` | Create | Admin |
| PUT | `/api/products/{id}` | Update | Admin |
| PATCH | `/api/products/{id}/status` | Toggle active | Admin |
| DELETE | `/api/products/{id}` | Delete | Admin |

## Variants - `/api/products/{productId}/variants`
| Method | Path | Description | Auth |
|--------|------|-------------|------|
| GET | `/api/products/{productId}/variants` | List by product | No |
| GET | `/api/products/{productId}/variants/{id}` | Get by ID | No |
| POST | `/api/products/{productId}/variants` | Add variant | Admin |
| PUT | `/api/products/{productId}/variants/{id}` | Update variant | Admin |
| DELETE | `/api/products/{productId}/variants/{id}` | Delete variant | Admin |

## Images - `/api/products/{productId}/images`
| Method | Path | Description | Auth |
|--------|------|-------------|------|
| GET | `/api/products/{productId}/images` | List images | No |
| POST | `/api/products/{productId}/images` | Add image | Admin |
| PUT | `/api/products/{productId}/images/{id}` | Update image | Admin |
| DELETE | `/api/products/{productId}/images/{id}` | Delete image | Admin |

## Reviews - `/api/reviews`
| Method | Path | Description | Auth |
|--------|------|-------------|------|
| GET | `/api/products/{productId}/reviews` | List by product | No |
| POST | `/api/products/{productId}/reviews` | Write review | Yes |
| PUT | `/api/reviews/{id}` | Update review | Yes |
| DELETE | `/api/reviews/{id}` | Delete review | Yes |
| POST | `/api/reviews/{id}/vote` | Upvote/downvote | Yes |
| POST | `/api/reviews/{id}/approve` | Approve review | Admin |

## Cart - `/api/carts`
| Method | Path | Description | Auth |
|--------|------|-------------|------|
| GET | `/api/carts` | Get user's cart | Yes |
| POST | `/api/carts` | Add item | Yes |
| PUT | `/api/carts/{itemId}` | Update quantity | Yes |
| DELETE | `/api/carts/{itemId}` | Remove item | Yes |
| DELETE | `/api/carts` | Clear cart | Yes |

## Wishlist - `/api/wishlists`
| Method | Path | Description | Auth |
|--------|------|-------------|------|
| GET | `/api/wishlists` | Get user's wishlist | Yes |
| POST | `/api/wishlists` | Add item | Yes |
| DELETE | `/api/wishlists/{itemId}` | Remove item | Yes |

---

## New Endpoints

### Addresses - `/api/addresses`
| Method | Path | Description | Auth |
|--------|------|-------------|------|
| GET | `/api/addresses` | List user's addresses | Yes |
| GET | `/api/addresses/{id}` | Get address | Yes |
| POST | `/api/addresses` | Create address | Yes |
| PUT | `/api/addresses/{id}` | Update address | Yes |
| PATCH | `/api/addresses/{id}/default` | Set as default | Yes |
| DELETE | `/api/addresses/{id}` | Delete address | Yes |

### Orders - `/api/orders`
| Method | Path | Description | Auth |
|--------|------|-------------|------|
| POST | `/api/orders/checkout` | Create order from cart | Yes |
| GET | `/api/orders` | List user's orders | Yes |
| GET | `/api/orders/{id}` | Get order detail | Yes |
| PATCH | `/api/orders/{id}/cancel` | Cancel order | Yes |
| PUT | `/api/orders/{id}/status` | Update status | Admin |

### Coupons - `/api/coupons`
| Method | Path | Description | Auth |
|--------|------|-------------|------|
| GET | `/api/coupons` | List active coupons | Yes |
| GET | `/api/coupons/{id}` | Get coupon detail | Admin |
| POST | `/api/coupons` | Create coupon | Admin |
| PUT | `/api/coupons/{id}` | Update coupon | Admin |
| PATCH | `/api/coupons/{id}/status` | Toggle active | Admin |
| DELETE | `/api/coupons/{id}` | Delete coupon | Admin |
| POST | `/api/coupons/validate` | Validate coupon code | Yes |

### Wallet - `/api/wallet`
| Method | Path | Description | Auth |
|--------|------|-------------|------|
| GET | `/api/wallet` | Get wallet balance | Yes |
| GET | `/api/wallet/transactions` | List transaction history | Yes |

### Returns - `/api/returns`
| Method | Path | Description | Auth |
|--------|------|-------------|------|
| POST | `/api/returns` | Create return request | Yes |
| GET | `/api/returns` | List user's returns | Yes |
| GET | `/api/returns/{id}` | Get return detail | Yes |
| PATCH | `/api/returns/{id}/status` | Update return status | Admin |
| PUT | `/api/returns/{id}/notes` | Add admin notes | Admin |

### Payments - `/api/payments`
| Method | Path | Description | Auth |
|--------|------|-------------|------|
| POST | `/api/payments/pay` | Process payment | Yes |
| GET | `/api/payments/{id}` | Get payment detail | Yes |
| POST | `/api/payments/{id}/refund` | Request refund | Admin |

### Shipping / Deliveries - `/api/deliveries`
| Method | Path | Description | Auth |
|--------|------|-------------|------|
| GET | `/api/deliveries/{orderId}/tracking` | Get tracking info | Yes |
| PUT | `/api/deliveries/{id}` | Update delivery | Admin |
