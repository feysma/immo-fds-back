# ImmoFDS Backend - Source Guide

Real estate agency backend built with Spring Boot 4.0, PostgreSQL, and JWT authentication.
This document maps every package and resource so you can navigate the codebase without guessing.

---

## Quick orientation

```
src/main/
  java/be/feysdigitalservices/immofds/
    config/             Framework and security configuration
    controller/
      admin/            Authenticated REST endpoints (ADMIN / SUPER_ADMIN)
      pub/              Public REST endpoints (no auth required)
    domain/
      entity/           JPA entities (database tables)
      enums/            Enum types used across entities and DTOs
    dto/
      request/          Incoming request bodies
      response/         Outgoing response bodies
    exception/          Custom exceptions + global error handler
    mapper/             MapStruct entity-to-DTO converters
    repository/         Spring Data JPA repository interfaces
    security/           JWT token provider, auth filter, UserDetailsService
    service/            Business logic (transactional)
    specification/      JPA Specification builders for dynamic queries
    validation/         Custom Bean Validation annotations + validators
  resources/
    application.yml     Main application configuration
    db/changelog/       Liquibase database migrations (XML)
```

---

## Java packages in detail

### `config/`

| File | Purpose |
|------|---------|
| `SecurityConfig.java` | Two Spring Security filter chains: public (permits `/api/v1/public/**`, `/api/v1/auth/**`, Swagger) and admin (requires ADMIN or SUPER_ADMIN role) |
| `JwtConfig.java` | `@ConfigurationProperties` for JWT secret, access-token expiration, refresh-token expiration |
| `OpenApiConfig.java` | Swagger/OpenAPI metadata and Bearer auth scheme |
| `WebConfig.java` | CORS configuration |

### `controller/admin/`

All endpoints under `/api/v1/admin/**` - requires authentication.

| File | Key endpoints |
|------|---------------|
| `AuthController.java` | `POST /auth/login`, `POST /auth/refresh`, `POST /auth/logout` |
| `AdminPropertyController.java` | Property CRUD, search, status updates, archive/delete |
| `AdminPropertyImageController.java` | Image upload, reorder, set primary, delete |
| `AdminContactController.java` | Contact request listing, status updates, admin notes |
| `AdminUserController.java` | User CRUD, activate/deactivate |

### `controller/pub/`

All endpoints under `/api/v1/public/**` - no authentication.

| File | Key endpoints |
|------|---------------|
| `PublicPropertyController.java` | Property search with filters, detail view, image retrieval, enum listings for dropdowns |
| `PublicContactController.java` | Submit general inquiry, visit request, or "sell your home" form |

### `domain/entity/`

JPA entities mapped to PostgreSQL tables.

| Entity | Table | Notes |
|--------|-------|-------|
| `Property.java` | `properties` | 30+ columns: details, location, features, timestamps. Has `@OneToMany` to `PropertyImage` |
| `PropertyImage.java` | `property_images` | Binary image data (`byte[]`), display order, primary flag. FK to `properties` with cascade delete |
| `ContactRequest.java` | `contact_requests` | Contact type, status tracking, optional property reference, admin notes |
| `User.java` | `users` | Email/password credentials, role, active flag |
| `RefreshToken.java` | `refresh_tokens` | JWT refresh token, expiry date. FK to `users` with cascade delete |

### `domain/enums/`

| Enum | Values |
|------|--------|
| `PropertyType` | APARTMENT, HOUSE, LAND, COMMERCIAL, ... |
| `TransactionType` | SALE, RENT |
| `PropertyStatus` | DRAFT, PUBLISHED, SOLD, RENTED, ARCHIVED |
| `ContactStatus` | NEW, IN_PROGRESS, RESOLVED, CLOSED |
| `ContactType` | GENERAL, SELL_YOUR_HOME, VISIT_REQUEST |
| `EnergyRating` | A_PLUS, A, B, C, D, E, F, G |
| `Province` | Belgian provinces (Flanders, Wallonia, Brussels, ...) |
| `UserRole` | ADMIN, SUPER_ADMIN |

### `dto/request/`

Request DTOs with Jakarta Bean Validation annotations.

| File | Used by |
|------|---------|
| `LoginRequest` | `AuthController` - email + password |
| `RefreshTokenRequest` | `AuthController` - refresh token string |
| `PropertyCreateRequest` | `AdminPropertyController` - full property creation |
| `PropertyUpdateRequest` | `AdminPropertyController` - property update |
| `PropertySearchCriteria` | Both public and admin search - filters (type, price range, province, features, ...) |
| `PropertyStatusUpdateRequest` | `AdminPropertyController` - status transition |
| `GeneralContactRequest` | `PublicContactController` - general inquiry |
| `SellYourHomeRequest` | `PublicContactController` - sell property form |
| `VisitRequestDto` | `PublicContactController` - visit request |
| `ContactStatusUpdateRequest` | `AdminContactController` - status change |
| `ContactNotesUpdateRequest` | `AdminContactController` - admin notes |
| `UserCreateRequest` | `AdminUserController` - new user |
| `UserUpdateRequest` | `AdminUserController` - edit user |
| `ImageReorderRequest` | `AdminPropertyImageController` - reorder images |

### `dto/response/`

| File | Purpose |
|------|---------|
| `AuthResponse` | JWT access token, refresh token, user info |
| `PropertySummaryResponse` | Compact property data for list views |
| `PropertyDetailResponse` | Full property data with image metadata |
| `PropertyImageResponse` | Image metadata (id, filename, content type, order, primary flag) |
| `ContactRequestResponse` | Contact request details |
| `UserResponse` | User info (no password) |
| `PageResponse<T>` | Generic paginated response wrapper (content, page, size, totalElements, totalPages) |
| `EnumValueResponse` | Enum value + display label for frontend dropdowns |
| `MessageResponse` | Simple success/info message |
| `ApiErrorResponse` | Error code, message, optional validation error list |

### `exception/`

| File | HTTP status | When |
|------|-------------|------|
| `ResourceNotFoundException` | 404 | Entity not found by ID or reference |
| `DuplicateResourceException` | 409 | Duplicate email, reference, etc. |
| `InvalidOperationException` | 400 | Invalid state transition or business rule violation |
| `InvalidTokenException` | 401 | JWT validation failure |
| `ImageProcessingException` | 400 | Image upload or processing error |
| `GlobalExceptionHandler` | - | `@RestControllerAdvice` that catches all the above and returns `ApiErrorResponse` |

### `mapper/`

MapStruct interfaces (Spring component model). Implementations are generated at compile time.

| File | Conversions |
|------|-------------|
| `PropertyMapper` | `Property` <-> `PropertyCreateRequest`, `PropertySummaryResponse`, `PropertyDetailResponse` |
| `ContactRequestMapper` | `ContactRequest` <-> `ContactRequestResponse` |
| `UserMapper` | `User` -> `UserResponse` |

### `repository/`

Spring Data JPA interfaces. Custom query methods beyond standard CRUD:

| File | Notable methods |
|------|-----------------|
| `PropertyRepository` | `findByReference()`, `findByReferenceAndStatus()` |
| `PropertyImageRepository` | Queries by image ID + property |
| `ContactRequestRepository` | Filter by status, contact type, or both |
| `UserRepository` | `findByEmail()` |
| `RefreshTokenRepository` | Lookup by token string, delete by user |

### `security/`

| File | Purpose |
|------|---------|
| `JwtTokenProvider.java` | Generate and validate JWT tokens, extract email from token |
| `JwtAuthenticationFilter.java` | `OncePerRequestFilter` that reads `Authorization: Bearer <token>` header |
| `UserDetailsServiceImpl.java` | Loads `User` entity by email for Spring Security authentication |

### `service/`

| File | Responsibilities |
|------|------------------|
| `PropertyService` | Property CRUD, public/admin search via Specifications, status transitions with validation |
| `PropertyImageService` | Upload (validates JPEG/PNG/WebP, max size), reorder, set primary, delete |
| `ContactRequestService` | Create from different form types, search with filters, status/notes updates |
| `AuthService` | Login (authenticate + issue JWT pair), refresh token rotation, logout |
| `UserService` | Create (with BCrypt password hashing), update, activate/deactivate, list |
| `ReferenceGeneratorService` | Generate unique property reference codes |

### `specification/`

| File | Purpose |
|------|---------|
| `PropertySpecification.java` | Builds JPA `Specification<Property>` predicates from `PropertySearchCriteria` - supports filtering by status, type, transaction, province, city, price range, surface range, bedroom count, and boolean features |

### `validation/`

| Annotation | Validator | Rule |
|------------|-----------|------|
| `@BelgianPostalCode` | `BelgianPostalCodeValidator` | 4-digit code within valid Belgian ranges per province |
| `@ValidImage` | `ValidImageValidator` | Content type must be JPEG, PNG, or WebP |

---

## Resources

### `application.yml`

Key settings (all overridable via environment variables):

- **Database**: PostgreSQL at `localhost:5432/immofds`
- **Hibernate**: `ddl-auto: validate` (schema managed by Liquibase, not Hibernate)
- **Liquibase**: changelog at `db/changelog/db.changelog-master.xml`
- **File uploads**: 10 MB per file, 50 MB per request
- **JWT**: configurable secret, 15 min access token, 7 day refresh token
- **Server port**: 8080
- **Swagger UI**: `/swagger-ui.html`

### `db/changelog/`

Liquibase XML migrations, executed in order by `db.changelog-master.xml`:

| File | What it does |
|------|--------------|
| `001-create-properties-table.xml` | `properties` table + 8 indexes (reference, status, type, transaction, province, city, price, postal code) |
| `002-create-property-images-table.xml` | `property_images` table + FK to properties (cascade delete) + 1 index |
| `003-create-contact-requests-table.xml` | `contact_requests` table + 3 indexes (status, contact type, created_at) |
| `004-create-users-table.xml` | `users` table + unique index on email |
| `005-create-refresh-tokens-table.xml` | `refresh_tokens` table + FK to users (cascade delete) + 2 indexes |
| `006-insert-default-super-admin.xml` | Seeds default super admin (`admin@immofds.be` / `Admin@2026!`) |

---

## Request flow

```
HTTP Request
  -> JwtAuthenticationFilter (extracts & validates token if present)
  -> SecurityConfig (checks route permissions)
  -> Controller (validates request body via Jakarta Validation)
  -> Service (business logic, transactions)
  -> Repository (JPA queries)
  -> Database (PostgreSQL)
  -> Mapper (entity -> response DTO)
  -> Controller (returns response)
```
