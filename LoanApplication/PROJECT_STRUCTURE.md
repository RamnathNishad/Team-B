# LoanApplication - Project Structure

## Directory Tree

```text
LoanApplication/
|-- .gitignore
|-- pom.xml
|-- .idea/
|-- .mvn/
|-- docs/
|   `-- api/
|       `-- POST_applyLoan.md
|-- src/
|   |-- main/
|   |   |-- java/
|   |   |   `-- com/
|   |   |       `-- smartLoan/
|   |   |           |-- LoanApplicationApp.java
|   |   |           |-- api/
|   |   |           |   `-- ApiErrorResponse.java
|   |   |           |-- config/
|   |   |           |   `-- (empty)
|   |   |           |-- controller/
|   |   |           |   |-- LoanApplicationController.java
|   |   |           |   `-- StatusUpdateRequest.java
|   |   |           |-- dto/
|   |   |           |   |-- LoanApplicationMultipartRequest.java
|   |   |           |   `-- LoanApplicationRequest.java
|   |   |           |-- entity/
|   |   |           |   `-- LoanApplication.java
|   |   |           |-- exception/
|   |   |           |   |-- DataConflictException.java
|   |   |           |   |-- DatabaseOperationException.java
|   |   |           |   |-- DatabaseUnavailableException.java
|   |   |           |   `-- GlobalExceptionHandler.java
|   |   |           |-- repository/
|   |   |           |   `-- LoanApplicationRepository.java
|   |   |           |-- service/
|   |   |           |   |-- LoanApplicationService.java
|   |   |           |   `-- LoanApplicationServiceImpl.java
|   |   |           `-- validation/
|   |   |               |-- LoanApplicationValidationException.java
|   |   |               `-- LoanApplicationValidator.java
|   |   `-- resources/
|   |       `-- application.properties
|   `-- test/
|       `-- java/
|           `-- com/
|               `-- smartLoan/
|                   |-- controller/
|                   |   `-- LoanApplicationControllerValidationTest.java
|                   |-- exception/
|                   |   `-- GlobalExceptionHandlerTest.java
|                   |-- service/
|                   |   `-- LoanApplicationServiceImplTest.java
|                   `-- validation/
|                       `-- LoanApplicationValidatorTest.java
`-- target/
```

## Module Snapshot

- `controller/` -> REST endpoints and request handling
- `service/` -> business logic and orchestration
- `repository/` -> persistence layer (Spring Data)
- `entity/` -> JPA entity models
- `dto/` -> request/transfer payloads
- `validation/` -> custom validation rules and exceptions
- `exception/` -> global exception mapping and domain/runtime exceptions
- `api/` -> standard API response models
- `docs/api/` -> endpoint-level API documentation

