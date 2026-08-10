# Security & Auth Hardening

Production hardening notes for the auth/OTP/security layers. Each item below was
verified live against the running dev server (port 8083).

## 1. Registration cannot escalate privileges

- **Before:** `POST /auth/register` accepted a client-supplied `roleId`, letting
  anyone register as `SUPER_ADMIN`/`ADMIN`.
- **Now:** `RegisterRequest.roleId` was removed. Every registration is created
  with the `USER` role (`AuthService.register`).
- Staff/admin accounts must be provisioned through the database/seeding or a
  future admin-only user-creation endpoint (not public registration).

## 2. Deactivated accounts get a friendly 403 on login

- `AuthService.login` previously surfaced Spring's `DisabledException` as a 500
  ("An unexpected error occurred").
- `GlobalExceptionHandler` now maps:
  - `DisabledException` -> `403 {"message": "Account has been deactivated"}`
  - `LockedException` -> `423 {"message": "Account is locked. Please try again later."}`
  - other `AuthenticationException` -> `401 {"message": "Invalid email/phone or password"}`

## 3. JSON bodies for security errors (no more empty 403)

- New `core/config/RestAuthenticationHandlers.java` (implements
  `AuthenticationEntryPoint` + `AccessDeniedHandler`) writes `ApiResponse` JSON
  for Spring-Security-level failures:
  - Missing/invalid/expired access token -> `401 {"message": "Your session has expired. Please sign in again."}`
  - Authorization failure -> `403 {"message": "You do not have permission to perform this action."}`
- Wired in `SecurityConfig.exceptionHandling`.

## 4. Token expiry driven by configuration

- `AuthResponse.expiresIn` now comes from `JwtTokenProvider.getAccessExpirationSeconds()`
  (reads `jwt.expiration-ms`) instead of a hardcoded `86400` in two places.
- Helpers `getAccessExpirationSeconds()` / `getRefreshExpirationSeconds()` added.

## 5. OTP rate limiting & brute-force protection

`modules/otp/service/OtpService.java` now enforces (in-memory):

| Rule | Value |
|---|---|
| OTP expiry | 5 minutes |
| Resend cooldown | 60 seconds -> `429 "Please wait N seconds before requesting a new OTP"` |
| Max resends per window | 5 -> `429 "Too many OTP requests. Please try again in a few minutes."` |
| Max verification attempts | 5 -> `429 "Too many incorrect attempts. Please request a new OTP."` |

New exceptions: `OtpCooldownException`, `OtpAttemptLimitException` (both extend `BaseException`).

> Dev note: `POST /auth/send-otp` returns the OTP in the response body — this is a
> dev convenience (no email/SMS gateway yet). Remove before production.

## Verification script (manual)

```powershell
# wrong password
POST /auth/login {"emailOrPhone":"user@example.com","password":"bad"}      -> 401 "Invalid email/phone or password"
# deactivated user
PATCH /users/{id}/deactivate  (as admin)  then  POST /auth/login           -> 403 "Account has been deactivated"
# no token
GET /users/me                                                            -> 401 JSON body
# otp cooldown
POST /otp/send x2 within 60s                                             -> 429 "Please wait 60 seconds..."
# login ok
POST /auth/login (valid)                                                 -> 200, expiresIn=86400 (from config)
```
