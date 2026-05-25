# POST `/api/v1/loan-applications/applyLoan`

Submit a new loan application with supporting documents.

---

## Overview

| Property        | Value                                          |
|-----------------|------------------------------------------------|
| **Method**      | `POST`                                         |
| **URL**         | `/api/v1/loan-applications/applyLoan`          |
| **Content-Type**| `multipart/form-data`                          |
| **Auth**        | _(as configured in your security layer)_       |

---

## Request

### Form Fields

All fields are sent as `multipart/form-data` parts (not JSON).

| Field            | Type           | Required | Constraints                                                                 | Description                                |
|------------------|----------------|----------|-----------------------------------------------------------------------------|--------------------------------------------|
| `customerId`     | `Long`         | ✅ Yes   | Must be `> 0`                                                               | ID of the customer submitting the loan     |
| `loanType`       | `String`       | ✅ Yes   | Case-insensitive. Accepted: `Personal`, `Home`, `Auto`                      | Type of loan being applied for             |
| `amount`         | `BigDecimal`   | ✅ Yes   | Must be `>= 0.01`                                                           | Requested loan amount                      |
| `tenure`         | `Integer`      | ✅ Yes   | Must be `> 0`                                                               | Loan duration in **months**                |
| `status`         | `String`       | ❌ No    | Must not be blank if provided. Typical values: `PENDING`, `UNDER_REVIEW`   | Initial status of the application          |
| `docAttachment1` | `File`         | ❌ No    | Max **5 MB**. Allowed types: `PDF`, `JPG`, `JPEG`, `PNG`                   | First supporting document (e.g. ID proof)  |
| `docAttachment2` | `File`         | ❌ No    | Max **5 MB**. Allowed types: `PDF`, `JPG`, `JPEG`, `PNG`                   | Second supporting document (e.g. income proof)  |
| `docAttachment3` | `File`         | ❌ No    | Max **5 MB**. Allowed types: `PDF`, `JPG`, `JPEG`, `PNG`                   | Third supporting document (e.g. address proof)  |

> **Note:** Document attachments are optional per field but the validator still checks file size and type when a file **is** provided.

---

## Response

### Success — `201 Created`

Returns the persisted `LoanApplication` entity as JSON.

```json
{
  "applicationId": 42,
  "customerId": 1,
  "loanType": "Home",
  "amount": 500000.00,
  "tenure": 60,
  "status": "PENDING",
  "docAttachment1": "<base64-encoded bytes>",
  "docAttachment2": "<base64-encoded bytes>",
  "docAttachment3": null
}
```

| Field            | Type         | Description                                              |
|------------------|--------------|----------------------------------------------------------|
| `applicationId`  | `Long`       | Auto-generated primary key                               |
| `customerId`     | `Long`       | Customer who submitted the application                   |
| `loanType`       | `String`     | Normalised loan type (`Personal`, `Home`, or `Auto`)     |
| `amount`         | `BigDecimal` | Approved loan amount                                     |
| `tenure`         | `Integer`    | Loan duration in months                                  |
| `status`         | `String`     | Current application status                               |
| `docAttachment1` | `byte[]`     | Raw bytes of the first document (may be `null`)          |
| `docAttachment2` | `byte[]`     | Raw bytes of the second document (may be `null`)         |
| `docAttachment3` | `byte[]`     | Raw bytes of the third document (may be `null`)          |

---

## HTTP Status Codes

| Status | Reason                  | When it occurs                                                                               |
|--------|-------------------------|----------------------------------------------------------------------------------------------|
| `201`  | Created                 | Application was successfully created and persisted                                            |
| `400`  | Bad Request             | Missing required fields, failed bean validation, invalid `loanType`, unsupported file type, file exceeds 5 MB, or a file could not be read |
| `409`  | Conflict                | A duplicate / conflicting record already exists (`DataConflictException`)                    |
| `500`  | Internal Server Error   | Unexpected server-side error or a database operation failure                                 |
| `503`  | Service Unavailable     | Database is unreachable (`DatabaseUnavailableException`)                                     |

---

## Error Response Body

All error responses share the same `ApiErrorResponse` shape:

```json
{
  "timestamp": "2026-05-21T10:30:00.123",
  "status": 400,
  "error": "Bad Request",
  "message": "<human-readable description of the problem>",
  "path": "/api/v1/loan-applications/applyLoan"
}
```

| Field       | Type     | Description                                      |
|-------------|----------|--------------------------------------------------|
| `timestamp` | `String` | ISO-8601 date-time when the error occurred       |
| `status`    | `int`    | HTTP status code                                 |
| `error`     | `String` | HTTP reason phrase                               |
| `message`   | `String` | Detailed error description                       |
| `path`      | `String` | Request URI that triggered the error             |

### Common `400` Error Messages

| Scenario                                  | `message` value                                                                               |
|-------------------------------------------|-----------------------------------------------------------------------------------------------|
| `customerId` is missing                   | `customerId is required`                                                                      |
| `customerId` ≤ 0                          | `customerId must be greater than 0`                                                           |
| `loanType` is missing                     | `loanType is required`                                                                        |
| `loanType` is not accepted                | `Invalid loanType. Supported values are: Personal, Home, Auto`                                |
| `amount` is missing                       | `amount is required`                                                                          |
| `amount` ≤ 0                              | `amount must be greater than 0`                                                               |
| `tenure` is missing                       | `tenure is required`                                                                          |
| `tenure` ≤ 0                              | `tenure must be greater than 0`                                                               |
| `status` is blank                         | `status must not be blank`                                                                    |
| Attachment exceeds 5 MB                   | `docAttachment1 exceeds maximum allowed size of 5MB`                                          |
| Attachment has unsupported type           | `docAttachment1 must be one of the supported file types: PDF, JPG, PNG`                       |
| Attachment has no/invalid extension       | `Uploaded documents must have a valid extension: PDF, JPG, PNG`                               |
| File bytes could not be read (`IOException`) | `Failed to read one or more uploaded documents. Please verify the uploaded files and try again.` |

---

## Example cURL Requests

### Minimal request (no documents)

```bash
curl -X POST http://localhost:8080/api/v1/loan-applications/applyLoan \
  -F "customerId=1" \
  -F "loanType=Personal" \
  -F "amount=20000.00" \
  -F "tenure=24" \
  -F "status=PENDING"
```

### Full request with all three document attachments

```bash
curl -X POST http://localhost:8080/api/v1/loan-applications/applyLoan \
  -F "customerId=1" \
  -F "loanType=Home" \
  -F "amount=500000.00" \
  -F "tenure=60" \
  -F "status=PENDING" \
  -F "docAttachment1=@/path/to/id_proof.pdf" \
  -F "docAttachment2=@/path/to/income_proof.pdf" \
  -F "docAttachment3=@/path/to/address_proof.png"
```

### Auto loan with only one document

```bash
curl -X POST http://localhost:8080/api/v1/loan-applications/applyLoan \
  -F "customerId=7" \
  -F "loanType=auto" \
  -F "amount=350000.00" \
  -F "tenure=36" \
  -F "status=PENDING" \
  -F "docAttachment1=@/path/to/driving_license.jpg"
```

### Expected `201 Created` response

```json
{
  "applicationId": 42,
  "customerId": 1,
  "loanType": "Home",
  "amount": 500000.00,
  "tenure": 60,
  "status": "PENDING",
  "docAttachment1": "...",
  "docAttachment2": "...",
  "docAttachment3": "..."
}
```

### Expected `400 Bad Request` — invalid loan type

```bash
curl -X POST http://localhost:8080/api/v1/loan-applications/applyLoan \
  -F "customerId=1" \
  -F "loanType=INVALID" \
  -F "amount=10000.00" \
  -F "tenure=12" \
  -F "status=PENDING"
```

```json
{
  "timestamp": "2026-05-21T10:30:00.123",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid loanType. Supported values are: Personal, Home, Auto",
  "path": "/api/v1/loan-applications/applyLoan"
}
```

### Expected `400 Bad Request` — file too large

```bash
curl -X POST http://localhost:8080/api/v1/loan-applications/applyLoan \
  -F "customerId=1" \
  -F "loanType=Personal" \
  -F "amount=10000.00" \
  -F "tenure=12" \
  -F "status=PENDING" \
  -F "docAttachment1=@/path/to/large_file_over_5mb.pdf"
```

```json
{
  "timestamp": "2026-05-21T10:30:00.123",
  "status": 400,
  "error": "Bad Request",
  "message": "docAttachment1 exceeds maximum allowed size of 5MB",
  "path": "/api/v1/loan-applications/applyLoan"
}
```

---

## Validation Rules Summary

```
Request
 ├── customerId      → not null, > 0
 ├── loanType        → not blank, case-insensitive match to: Personal | Home | Auto
 ├── amount          → not null, ≥ 0.01
 ├── tenure          → not null, > 0 (months)
 ├── status          → optional; if provided, must not be blank
 ├── docAttachment1  → optional; if provided: ≤ 5 MB, extension/MIME in {pdf, jpg, jpeg, png}
 ├── docAttachment2  → optional; same rules as docAttachment1
 └── docAttachment3  → optional; same rules as docAttachment1
```

---

## Notes

- **`loanType` normalisation** — the value is accepted in any casing (`home`, `HOME`, `Home`) and is stored normalised as `Personal`, `Home`, or `Auto`.
- **Documents are stored as binary** (LOB/BLOB) directly in the database; they are not written to the file system.
- **`status` default** — if omitted, the application layer is responsible for assigning a default status (typically `PENDING`).

