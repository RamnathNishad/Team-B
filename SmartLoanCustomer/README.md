# SmartLoanCustomer

SmartLoanCustomer is a Spring Boot customer-auth and profile service for loan onboarding.

## Tech Stack

- Java 17
- Spring Boot (Web, Data JPA, Validation)
- H2 in-memory database
- Swagger/OpenAPI via SpringDoc

## Prerequisites

- JDK 17+
- Maven 3.9+ (or use `mvnw`/`mvnw.cmd`)

## Run the Project

### Windows (PowerShell)

```powershell
cd "C:\Users\dilip.chauhan\Downloads\SampleProject\Team-B\SmartLoanCustomer"
.\mvnw.cmd spring-boot:run
```

### Build (without tests)

```powershell
cd "C:\Users\dilip.chauhan\Downloads\SampleProject\Team-B\SmartLoanCustomer"
.\mvnw.cmd clean package -DskipTests
```

App default URL: `http://localhost:8083`

## API Docs

- Swagger UI: `http://localhost:8083/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8083/v3/api-docs`

## Database Access (H2)

Configured in `src/main/resources/application.yaml`:

- JDBC URL: `jdbc:h2:mem:smartloandb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
- Username: `sa`
- Password: empty

H2 Console:

- URL: `http://localhost:8083/h2-console`
- Driver: `org.h2.Driver`
- JDBC URL: `jdbc:h2:mem:smartloandb`
- User: `sa`
- Password: *(empty)*

## Authentication Model

- Login returns a session token.
- Protected endpoints require header:

```http
Authorization: Bearer <token>
```

## Endpoint Catalog

Base URL: `http://localhost:8083`

### Auth APIs (`/api/auth`)

#### 1) Register

- **Method**: `POST`
- **Path**: `/api/auth/register`
- **Auth**: Not required
- **Request Body**:

```json
{
  "email": "alice@example.com",
  "password": "Password@123",
  "firstName": "Alice",
  "lastName": "Anderson",
  "dob": "1990-01-15",
  "phoneNumber": "+1-555-0101",
  "address": "123 Main St, Springfield"
}
```

- **Success Response (200)**:

```json
{
  "message": "Registration successful. Verify your email using the verification token from the customer record.",
  "token": null
}
```

#### 2) Verify Email

- **Method**: `GET`
- **Path**: `/api/auth/verify-email?token=<verification_token>`
- **Auth**: Not required
- **Success Response (200)**:

```json
{
  "message": "Email verification completed.",
  "token": null
}
```

#### 3) Login

- **Method**: `POST`
- **Path**: `/api/auth/login`
- **Auth**: Not required
- **Request Body**:

```json
{
  "email": "alice@example.com",
  "password": "Password@123",
  "rememberMe": true
}
```

- **Success Response (200)**:

```json
{
  "token": "<session-token>",
  "expiresAt": "2026-05-25T14:30:00",
  "email": "alice@example.com",
  "firstName": "Alice",
  "lastName": "Anderson"
}
```

#### 4) Request Password Reset

- **Method**: `POST`
- **Path**: `/api/auth/request-password-reset`
- **Auth**: Not required
- **Request Body**:

```json
{
  "email": "alice@example.com"
}
```

- **Success Response (200)**:

```json
{
  "message": "Password reset token created.",
  "token": "<reset-token>"
}
```

#### 5) Reset Password

- **Method**: `POST`
- **Path**: `/api/auth/reset-password`
- **Auth**: Not required
- **Request Body**:

```json
{
  "resetToken": "<reset-token>",
  "newPassword": "NewPassword@123"
}
```

- **Success Response (200)**:

```json
{
  "message": "Password reset successful.",
  "token": null
}
```

### Customer APIs (`/api/customer`)

#### 6) Get Profile

- **Method**: `GET`
- **Path**: `/api/customer/profile`
- **Auth**: Required (`Bearer token`)
- **Success Response (200)**:

```json
{
  "customerId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "email": "alice@example.com",
  "firstName": "Alice",
  "lastName": "Anderson",
  "dob": "1990-01-15",
  "phoneNumber": "+1-555-0101",
  "address": "123 Main St, Springfield",
  "accountStatus": "Active",
  "emailVerified": true,
  "lastLoginAt": "2026-05-25T13:30:00",
  "createdDate": "2026-05-25T10:00:00"
}
```

#### 7) Update Profile

- **Method**: `PUT`
- **Path**: `/api/customer/profile`
- **Auth**: Required (`Bearer token`)
- **Request Body**:

```json
{
  "firstName": "Alice",
  "lastName": "A.",
  "dob": "1990-01-15",
  "phoneNumber": "+1-555-0109",
  "address": "456 Oak Ave, Springfield"
}
```

- **Success Response (200)**: same shape as **Get Profile**

## Recommended End-to-End API Flow

1. `POST /api/auth/register`
2. Fetch verification token from DB (`customer.verification_token`)
3. `GET /api/auth/verify-email?token=...`
4. `POST /api/auth/login` and store returned `token`
5. Call protected APIs with `Authorization: Bearer <token>`

## Useful SQL Snippets (H2)

```sql
-- Verification token
SELECT email, verification_token FROM customer WHERE email = 'alice@example.com';

-- Reset token
SELECT email, reset_token FROM customer WHERE email = 'alice@example.com';

-- Latest session token
SELECT token, customer_id, created_at, expires_at
FROM session_token
ORDER BY created_at DESC;
```

## Error Responses

Global error handling is defined in `GlobalExceptionHandler`:

- `400 Bad Request`: validation issues / invalid request
- `401 Unauthorized`: missing/invalid/expired bearer token
- `404 Not Found`: resource or token not found
- `500 Internal Server Error`: unexpected server errors

## Docker (Optional)

```powershell
cd "C:\Users\dilip.chauhan\Downloads\SampleProject\Team-B\SmartLoanCustomer"
docker build -t smart-loan-customer .
docker run --rm -p 8083:8083 smart-loan-customer
```

## Notes

- DB is in-memory (`mem`), so data is reset when app process ends.
- For persistent data, switch JDBC URL to file-based H2 or external DB (MySQL/PostgreSQL).

