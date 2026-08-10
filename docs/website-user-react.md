# E-Commerce — React User Website

> **Target:** Web Browser (End Users)  
> **Architecture:** Feature-based + MVVM-like (hooks + state management)  
> **Tech:** React 18+, TypeScript, Vite, React Router, Zustand, React Query, Axios, Tailwind CSS  
> **Base URL:** `http://host:8083/api/v1`

---

## Project Structure (Mirrors Android Feature Pattern)

```
src/
├── core/
│   ├── config/
│   │   └── environment.ts           # BASE_URL per env (dev/staging/prod)
│   ├── di/                          # Service locator / context providers
│   │   ├── AuthProvider.tsx
│   │   ├── QueryProvider.tsx
│   │   └── ThemeProvider.tsx
│   ├── hooks/                       # Shared hooks
│   │   ├── useAuth.ts
│   │   ├── useDebounce.ts
│   │   └── usePagination.ts
│   ├── lib/
│   │   ├── api.ts                   # Axios instance + interceptors
│   │   ├── authInterceptor.ts       # Bearer token + refresh logic
│   │   └── storage.ts              # Token persistence (localStorage)
│   ├── types/
│   │   ├── api.ts                   # ApiResponse<T>, Pagination
│   │   └── common.ts               # Shared enums, constants
│   ├── ui/
│   │   └── components/              # Shared UI components
│   │       ├── Button.tsx
│   │       ├── Input.tsx
│   │       ├── Modal.tsx
│   │       ├── Spinner.tsx
│   │       ├── EmptyState.tsx
│   │       ├── ErrorBoundary.tsx
│   │       ├── ProductCard.tsx
│   │       ├── RatingStars.tsx
│   │       ├── PriceDisplay.tsx
│   │       └── Pagination.tsx
│   ├── layouts/
│   │   ├── MainLayout.tsx           # Header + Footer + Outlet
│   │   ├── AuthLayout.tsx           # Login/register layout
│   │   └── BottomNav.tsx            # Mobile bottom nav
│   └── utils/
│       ├── format.ts                # Currency, date, text formatters
│       └── validation.ts            # Form validation rules
├── features/
│   ├── auth/
│   │   ├── api/
│   │   │   └── authApi.ts           # Axios calls
│   │   ├── components/
│   │   │   ├── LoginForm.tsx
│   │   │   ├── RegisterForm.tsx
│   │   │   ├── OtpInput.tsx
│   │   │   └── SocialLoginButton.tsx
│   │   ├── hooks/
│   │   │   ├── useAuth.ts          # Login/register/logout state
│   │   │   ├── useLogin.ts
│   │   │   └── useRegister.ts
│   │   ├── pages/
│   │   │   ├── LoginPage.tsx
│   │   │   ├── RegisterPage.tsx
│   │   │   └── OtpVerificationPage.tsx
│   │   ├── store/
│   │   │   └── authStore.ts         # Zustand store
│   │   └── types/
│   │       ├── auth.ts              # LoginRequest, RegisterRequest, AuthResponse
│   │       └── user.ts              # User
│   ├── home/
│   │   ├── api/
│   │   │   └── homeApi.ts
│   │   ├── components/
│   │   │   ├── HeroBanner.tsx
│   │   │   ├── FeaturedProducts.tsx
│   │   │   ├── CategoryGrid.tsx
│   │   │   └── BrandStrip.tsx
│   │   ├── hooks/
│   │   │   └── useHomeData.ts
│   │   ├── pages/
│   │   │   └── HomePage.tsx
│   │   └── types/
│   │       └── home.ts
│   ├── product/
│   │   ├── api/
│   │   │   └── productApi.ts         # getProducts, getProductByUuid, getSimilarProducts
│   │   ├── components/
│   │   │   ├── ProductGrid.tsx
│   │   │   ├── ProductListItem.tsx
│   │   │   ├── ProductImageGallery.tsx
│   │   │   ├── VariantSelector.tsx
│   │   │   ├── ReviewList.tsx
│   │   │   ├── ReviewForm.tsx
│   │   │   ├── FilterSidebar.tsx
│   │   │   └── SortDropdown.tsx
│   │   ├── hooks/
│   │   │   ├── useProductList.ts     # pagination + filters + search
│   │   │   └── useProductDetail.ts
│   │   ├── pages/
│   │   │   ├── ProductListPage.tsx
│   │   │   ├── ProductDetailPage.tsx
│   │   │   └── SearchResultsPage.tsx
│   │   └── types/
│   │       ├── product.ts
│   │       └── review.ts
│   ├── cart/
│   │   ├── api/
│   │   │   └── cartApi.ts
│   │   ├── components/
│   │   │   ├── CartItemRow.tsx
│   │   │   ├── CartSummary.tsx
│   │   │   └── CartBadge.tsx          # Header icon with count
│   │   ├── hooks/
│   │   │   └── useCart.ts
│   │   ├── pages/
│   │   │   └── CartPage.tsx
│   │   ├── store/
│   │   │   └── cartStore.ts           # Zustand: optimistic cart state
│   │   └── types/
│   │       └── cart.ts
│   ├── checkout/
│   │   ├── api/
│   │   │   ├── checkoutApi.ts
│   │   │   └── couponApi.ts
│   │   ├── components/
│   │   │   ├── AddressForm.tsx
│   │   │   ├── AddressSelector.tsx
│   │   │   ├── PaymentMethodSelector.tsx
│   │   │   ├── CouponInput.tsx
│   │   │   └── OrderSummary.tsx
│   │   ├── hooks/
│   │   │   ├── useCheckout.ts
│   │   │   └── useValidateCoupon.ts
│   │   ├── pages/
│   │   │   ├── CheckoutPage.tsx
│   │   │   └── OrderConfirmationPage.tsx
│   │   └── types/
│   │       └── checkout.ts
│   ├── order/
│   │   ├── api/
│   │   │   └── orderApi.ts
│   │   ├── components/
│   │   │   ├── OrderCard.tsx
│   │   │   ├── OrderTimeline.tsx
│   │   │   └── OrderItemRow.tsx
│   │   ├── hooks/
│   │   │   ├── useOrders.ts
│   │   │   └── useOrderDetail.ts
│   │   ├── pages/
│   │   │   ├── OrderListPage.tsx
│   │   │   └── OrderDetailPage.tsx
│   │   └── types/
│   │       └── order.ts
│   ├── wishlist/
│   │   ├── api/
│   │   │   └── wishlistApi.ts
│   │   ├── hooks/
│   │   │   └── useWishlist.ts
│   │   ├── pages/
│   │   │   └── WishlistPage.tsx
│   │   └── types/
│   │       └── wishlist.ts
│   ├── address/
│   │   ├── api/
│   │   │   └── addressApi.ts
│   │   ├── components/
│   │   │   ├── AddressCard.tsx
│   │   │   └── AddressForm.tsx
│   │   ├── hooks/
│   │   │   └── useAddresses.ts
│   │   ├── pages/
│   │   │   └── AddressListPage.tsx
│   │   └── types/
│   │       └── address.ts
│   ├── profile/
│   │   ├── api/
│   │   │   └── profileApi.ts
│   │   ├── components/
│   │   │   ├── ProfileForm.tsx
│   │   │   └── ChangePasswordForm.tsx
│   │   ├── hooks/
│   │   │   └── useProfile.ts
│   │   ├── pages/
│   │   │   ├── ProfilePage.tsx
│   │   │   └── EditProfilePage.tsx
│   │   └── types/
│   │       └── profile.ts
│   ├── wallet/
│   │   ├── api/
│   │   │   └── walletApi.ts
│   │   ├── components/
│   │   │   ├── WalletBalance.tsx
│   │   │   └── TransactionList.tsx
│   │   ├── hooks/
│   │   │   └── useWallet.ts
│   │   ├── pages/
│   │   │   └── WalletPage.tsx
│   │   └── types/
│   │       └── wallet.ts
│   ├── notification/
│   │   ├── api/
│   │   │   └── notificationApi.ts
│   │   ├── components/
│   │   │   ├── NotificationBell.tsx      # Header icon
│   │   │   └── NotificationList.tsx
│   │   ├── hooks/
│   │   │   └── useNotifications.ts
│   │   ├── pages/
│   │   │   └── NotificationsPage.tsx
│   │   └── types/
│   │       └── notification.ts
│   └── chat/
│       ├── api/
│       │   └── chatApi.ts
│       ├── components/
│       │   ├── ChatRoomList.tsx
│       │   └── ChatMessageThread.tsx
│       ├── hooks/
│       │   └── useChat.ts
│       ├── pages/
│       │   ├── ChatListPage.tsx
│       │   └── ChatDetailPage.tsx
│       └── types/
│           └── chat.ts
├── App.tsx
├── main.tsx
└── routes.tsx                          # React Router config
```

---

## Dependencies (`package.json`)

```json
{
  "name": "ecommerce-user-web",
  "private": true,
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "preview": "vite preview",
    "lint": "eslint . --ext ts,tsx",
    "test": "vitest"
  },
  "dependencies": {
    "react": "^18.3.0",
    "react-dom": "^18.3.0",
    "react-router-dom": "^6.28.0",
    "@tanstack/react-query": "^5.62.0",
    "zustand": "^5.0.0",
    "axios": "^1.7.9",
    "react-hook-form": "^7.54.0",
    "zod": "^3.24.0",
    "@hookform/resolvers": "^3.9.0",
    "date-fns": "^4.1.0",
    "clsx": "^2.1.0",
    "react-hot-toast": "^2.4.0"
  },
  "devDependencies": {
    "@types/react": "^18.3.0",
    "@types/react-dom": "^18.3.0",
    "@vitejs/plugin-react": "^4.3.0",
    "typescript": "^5.7.0",
    "vite": "^6.0.0",
    "vitest": "^2.1.0",
    "@testing-library/react": "^16.1.0",
    "@testing-library/jest-dom": "^6.6.0",
    "msw": "^2.6.0",
    "eslint": "^9.0.0",
    "tailwindcss": "^4.0.0",
    "@tailwindcss/vite": "^4.0.0"
  }
}
```

---

## Architecture Pattern (MVVM-like for React)

```
User Action → Component → Hook / Store → API / Query → Axios → Backend
                    ↑                                │
                    └─────────── State ──────────────┘
```

### Per-Feature Example: Product List

```typescript
// ── types/product.ts ──
export interface Product {
  uuid: string;
  sku: string;
  name: string;
  slug: string;
  description: string;
  basePrice: number;
  primaryImage: string;
  isActive: boolean;
  category: { slug: string; name: string };
  brand: { slug: string; name: string };
}

export interface ProductListState {
  products: Product[];
  isLoading: boolean;
  error: string | null;
  currentPage: number;
  hasMore: boolean;
  totalElements: number;
}

// ── api/productApi.ts ──
import axiosInstance from '@/core/lib/api';
import type { ApiResponse, Pagination } from '@/core/types/api';
import type { Product } from '../types/product';

interface ProductListResponse {
  content: Product[];
  pagination: Pagination;
}

export const productApi = {
  getProducts: async (params: {
    page?: number;
    size?: number;
    search?: string;
    categorySlug?: string;
    minPrice?: number;
    maxPrice?: number;
    sortBy?: string;
    sortDir?: string;
  }): Promise<ProductListResponse> => {
    const { data } = await axiosInstance.get<ApiResponse<Product[]>>('/products', { params });
    return {
      content: data.response,
      pagination: data.pagination!,
    };
  },

  getProductByUuid: async (uuid: string): Promise<Product> => {
    const { data } = await axiosInstance.get<ApiResponse<Product>>(`/products/${uuid}`);
    return data.response;
  },

  getSimilarProducts: async (uuid: string, limit = 10): Promise<Product[]> => {
    const { data } = await axiosInstance.get<ApiResponse<Product[]>>(`/products/${uuid}/similar`, {
      params: { limit },
    });
    return data.response;
  },
};

// ── hooks/useProductList.ts ──
import { useInfiniteQuery } from '@tanstack/react-query';
import { productApi } from '../api/productApi';

export function useProductList(filters: {
  search?: string;
  categorySlug?: string;
  sortBy?: string;
}) {
  return useInfiniteQuery({
    queryKey: ['products', filters],
    queryFn: ({ pageParam = 0 }) =>
      productApi.getProducts({ ...filters, page: pageParam, size: 20 }),
    getNextPageParam: (lastPage, allPages) =>
      lastPage.hasMore ? allPages.length : undefined,
    initialPageParam: 0,
  });
}

// ── pages/ProductListPage.tsx ──
import { useProductList } from '@/features/product/hooks/useProductList';
import { ProductGrid } from '@/features/product/components/ProductGrid';
import { FilterSidebar } from '@/features/product/components/FilterSidebar';
import { useState } from 'react';

export default function ProductListPage() {
  const [filters, setFilters] = useState({});
  const { data, isLoading, fetchNextPage, hasNextPage } = useProductList(filters);

  return (
    <div className="flex gap-6">
      <FilterSidebar onFilterChange={setFilters} />
      <div className="flex-1">
        {isLoading && <Spinner />}
        <ProductGrid
          products={data?.pages.flatMap(p => p.content) ?? []}
          onLoadMore={() => hasNextPage && fetchNextPage()}
        />
      </div>
    </div>
  );
}
```

---

## Routing Structure

```typescript
// src/routes.tsx
const router = createBrowserRouter([
  {
    element: <AuthLayout />,
    children: [
      { path: '/login', element: <LoginPage /> },
      { path: '/register', element: <RegisterPage /> },
      { path: '/verify-otp', element: <OtpVerificationPage /> },
    ],
  },
  {
    element: <MainLayout />,
    children: [
      { index: true, element: <HomePage /> },
      { path: '/products', element: <ProductListPage /> },
      { path: '/products/:slug', element: <ProductDetailPage /> },
      { path: '/search', element: <SearchResultsPage /> },
      { path: '/cart', element: <CartPage /> },
      { path: '/checkout', element: <CheckoutPage /> },
      { path: '/order-confirmation/:uuid', element: <OrderConfirmationPage /> },
      { path: '/orders', element: <OrderListPage /> },
      { path: '/orders/:uuid', element: <OrderDetailPage /> },
      { path: '/wishlist', element: <WishlistPage /> },
      { path: '/addresses', element: <AddressListPage /> },
      { path: '/profile', element: <ProfilePage /> },
      { path: '/profile/edit', element: <EditProfilePage /> },
      { path: '/wallet', element: <WalletPage /> },
      { path: '/notifications', element: <NotificationsPage /> },
      { path: '/support', element: <ChatListPage /> },
      { path: '/support/:roomId', element: <ChatDetailPage /> },
    ],
  },
]);
```

---

## Auth Flow

```typescript
// Axios interceptor (core/lib/authInterceptor.ts)
import axios from 'axios';
import { useAuthStore } from '@/features/auth/store/authStore';

axiosInstance.interceptors.request.use((config) => {
  const token = useAuthStore.getState().accessToken;
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

axiosInstance.interceptors.response.use(
  (res) => res,
  async (error) => {
    if (error.response?.status === 401 && !error.config._retry) {
      error.config._retry = true;
      try {
        const newToken = await refreshTokens();
        error.config.headers.Authorization = `Bearer ${newToken}`;
        return axiosInstance(error.config);
      } catch {
        useAuthStore.getState().logout();
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);
```

---

## API Endpoints Used (User Web)

| Feature | Endpoints |
|---|---|
| **Auth** | `POST /auth/register`, `POST /auth/login`, `POST /auth/refresh`, `POST /auth/send-otp`, `POST /auth/verify-otp`, `POST /auth/logout` |
| **Home** | `GET /home` |
| **Products** | `GET /products` (paginated, filtered), `GET /products/{uuid}`, `GET /products/{uuid}/similar` |
| **Categories** | `GET /categories`, `GET /categories/tree` |
| **Cart** | `GET /carts`, `POST /carts/{productUuid}`, `PATCH /carts/{itemUuid}`, `DELETE /carts/{itemUuid}`, `DELETE /carts` |
| **Orders** | `POST /orders/checkout`, `GET /orders`, `GET /orders/{uuid}`, `PATCH /orders/{uuid}/cancel` |
| **Wishlist** | `GET /wishlist`, `POST /wishlist/{productUuid}`, `DELETE /wishlist/{itemUuid}` |
| **Reviews** | `POST /reviews`, `POST /reviews/{id}/vote` |
| **Addresses** | `GET /addresses`, `POST /addresses`, `PUT /addresses/{uuid}`, `DELETE /addresses/{uuid}` |
| **Wallet** | `GET /wallets/me`, `GET /wallets/me/transactions` |
| **Profile** | `GET /users/me`, `PUT /users/me`, `PUT /users/me/password` |
| **Notifications** | `GET /notifications`, `GET /notifications/unread-count`, `PUT /notifications/{uuid}/read`, `PUT /notifications/read-all` |
| **Chat** | `GET /chat/rooms`, `POST /chat/rooms`, `GET /chat/rooms/{uuid}/messages`, `POST /chat/rooms/{uuid}/messages` |

---

## Environment Config

```typescript
// src/core/config/environment.ts
export const ENV = {
  dev: {
    baseUrl: 'http://localhost:8083/api/v1',
    wsUrl: 'ws://localhost:8083/api/v1/ws',
  },
  staging: {
    baseUrl: 'https://staging-api.example.com/api/v1',
    wsUrl: 'wss://staging-api.example.com/api/v1/ws',
  },
  production: {
    baseUrl: 'https://api.example.com/api/v1',
    wsUrl: 'wss://api.example.com/api/v1/ws',
  },
} as const;

export const currentEnv = ENV[import.meta.env.VITE_APP_ENV ?? 'dev'];
```

---

## Key Design Decisions

| Decision | Choice | Rationale |
|---|---|---|
| **Build Tool** | Vite | Fast HMR, TypeScript-native, tree-shaking |
| **State** | Zustand (global) + React Query (server) | Zustand for client state (cart, auth); RQ for server cache |
| **Forms** | react-hook-form + zod | Type-safe validation, minimal re-renders |
| **API Client** | Axios + interceptor | Interceptor for auth token + auto-refresh |
| **Styling** | Tailwind CSS 4 | Utility-first, responsive, no runtime |
| **Routing** | React Router v6 | Nested layouts, loaders, params |
| **Testing** | Vitest + Testing Library + MSW | Fast, component-focused, mock service worker |
| **Code Quality** | TypeScript strict, ESLint, Prettier | Type safety, consistent style |
