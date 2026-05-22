package com.smartLoan.controller;

import com.smartLoan.dto.LoanApplicationMultipartRequest;
import com.smartLoan.dto.LoanApplicationRequest;
import com.smartLoan.entity.LoanApplication;
import com.smartLoan.service.LoanApplicationService;
import com.smartLoan.validation.LoanApplicationValidator;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * REST API Controller for Loan Application operations.
 *
 * <ul>
 *   <li>All endpoints are prefixed with {@code /api/v1/loan-applications}</li>
 *   <li>Constructor injection (@RequiredArgsConstructor from Lombok) injects the service</li>
 *   <li>Proper HTTP status codes: 201 Created, 200 OK, 404 Not Found, 500 Server Error</li>
 * </ul>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/loan-applications")
@Validated
@RequiredArgsConstructor
public class LoanApplicationController {

    private final LoanApplicationService loanApplicationService;
    private final LoanApplicationValidator loanApplicationValidator;

    // ─────────────────────────────────────────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Create a new loan application with multipart form data.
     *
     * <p>
     * Accepts all loan application fields as form parameters + document attachments as file uploads.
     * Content-Type must be {@code multipart/form-data}.
     * </p>
     *
     * <p>
     * <strong>Form Data Example (using curl):</strong>
     * <pre>
     * curl -X POST http://localhost:8080/api/v1/loan-applications/applyLoan \
     *   -F "customerId=1" \
     *   -F "loanType=HOME" \
     *   -F "amount=500000.00" \
     *   -F "tenure=60" \
     *   -F "status=PENDING" \
     *   -F "docAttachment1=@/path/to/id_proof.pdf" \
     *   -F "docAttachment2=@/path/to/income_proof.pdf" \
     *   -F "docAttachment3=@/path/to/address_proof.pdf"
     * </pre>
     * </p>
     *
     * @param formData         multipart request containing loan fields and files
     * @return HTTP 201 (Created) with the persisted {@link LoanApplication} entity
     * @throws IOException if file reading fails
     */
    @PostMapping(value = "/applyLoan", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LoanApplication> createLoanApplication(
          @Valid @ModelAttribute LoanApplicationMultipartRequest formData) throws IOException {

        log.info("POST /api/v1/loan-applications/applyLoan - Creating new loan application for customerId={}",
                formData.getCustomerId());

        loanApplicationValidator.validateCreateLoanRequest(
                formData.getLoanType(),
                formData.getDocAttachment1(),
                formData.getDocAttachment2(),
                formData.getDocAttachment3());

        String normalizedLoanType = loanApplicationValidator.normalizeLoanType(formData.getLoanType());

        // Convert MultipartFiles to byte arrays
        byte[] doc1Bytes = formData.getDocAttachment1() != null ? formData.getDocAttachment1().getBytes() : null;
        byte[] doc2Bytes = formData.getDocAttachment2() != null ? formData.getDocAttachment2().getBytes() : null;
        byte[] doc3Bytes = formData.getDocAttachment3() != null ? formData.getDocAttachment3().getBytes() : null;

        // Build the request DTO from individual parameters
        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .customerId(formData.getCustomerId())
                .loanType(normalizedLoanType)
                .amount(formData.getAmount())
                .tenure(formData.getTenure())
                .status(formData.getStatus())
                .docAttachment1(doc1Bytes)
                .docAttachment2(doc2Bytes)
                .docAttachment3(doc3Bytes)
                .build();

        LoanApplication created = loanApplicationService.createLoanApplication(request);

        log.info("Loan application persisted successfully with applicationId={}",
                created.getApplicationId());

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // READ
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Retrieve a loan application by its ID.
     *
     * @param applicationId the auto-generated application ID
     * @return HTTP 200 (OK) with the matching {@link LoanApplication}
     * @throws jakarta.persistence.EntityNotFoundException if no record found (HTTP 404)
     */
    @GetMapping("/{applicationId}")
    public ResponseEntity<LoanApplication> getLoanApplicationById(
            @PathVariable Long applicationId) {

        log.info("GET /api/v1/loan-applications/{} - Fetching application", applicationId);
        LoanApplication application = loanApplicationService.getLoanApplicationById(applicationId);
        return ResponseEntity.ok(application);
    }

    /**
     * Retrieve all loan applications for a specific customer.
     *
     * @param customerId the customer's ID
     * @return HTTP 200 (OK) with list of {@link LoanApplication} records
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<LoanApplication>> getLoanApplicationsByCustomerId(
            @PathVariable Long customerId) {

        log.info("GET /api/v1/loan-applications/customer/{} - Fetching customer applications", customerId);
        List<LoanApplication> applications = loanApplicationService.getLoanApplicationsByCustomerId(customerId);
        return ResponseEntity.ok(applications);
    }

    /**
     * Retrieve all loan applications filtered by status.
     *
     * @param status the application status (e.g. "PENDING", "APPROVED", "REJECTED")
     * @return HTTP 200 (OK) with list of {@link LoanApplication} records
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<LoanApplication>> getLoanApplicationsByStatus(
            @PathVariable String status) {

        log.info("GET /api/v1/loan-applications/status/{} - Fetching applications by status", status);
        List<LoanApplication> applications = loanApplicationService.getLoanApplicationsByStatus(status);
        return ResponseEntity.ok(applications);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UPDATE
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Update the status of an existing loan application.
     *
     * <p>
     * <strong>Request Body Example:</strong>
     * <pre>
     * {
     *   "status": "APPROVED"
     * }
     * </pre>
     * </p>
     *
     * @param applicationId the application to update
     * @param statusUpdate  a simple object carrying the new status string
     * @return HTTP 200 (OK) with the updated {@link LoanApplication}
     */
    @PutMapping("/{applicationId}/status")
    public ResponseEntity<LoanApplication> updateLoanApplicationStatus(
            @PathVariable Long applicationId,
            @Valid @RequestBody StatusUpdateRequest statusUpdate) {

        log.info("PUT /api/v1/loan-applications/{}/status - Updating status to '{}'",
                applicationId, statusUpdate.getStatus());
        LoanApplication updated = loanApplicationService.updateLoanApplicationStatus(
                applicationId, statusUpdate.getStatus());
        return ResponseEntity.ok(updated);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Delete a loan application by its ID.
     *
     * @param applicationId the application to delete
     * @return HTTP 204 (No Content)
     */
    @DeleteMapping("/{applicationId}")
    public ResponseEntity<Void> deleteLoanApplication(
            @PathVariable Long applicationId) {

        log.info("DELETE /api/v1/loan-applications/{} - Deleting application", applicationId);
        loanApplicationService.deleteLoanApplication(applicationId);
        return ResponseEntity.noContent().build();
    }
}

