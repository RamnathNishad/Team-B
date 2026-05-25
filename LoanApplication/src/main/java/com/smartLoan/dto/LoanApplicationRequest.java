package com.smartLoan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object used to pass all loan application input parameters
 * into the service layer.
 *
 * Using a dedicated DTO keeps the service API clean and decouples the
 * controller / test code from the JPA entity internals.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplicationRequest {

    /** ID of the customer submitting the application. */
    private Long customerId;

    /**
     * Type of loan being requested.
     * Expected values: HOME, PERSONAL, VEHICLE, EDUCATION (not exhaustive).
     */
    private String loanType;

    /** Requested loan amount (e.g. 500000.00). */
    private BigDecimal amount;

    /** Loan tenure in months (e.g. 60 for 5 years). */
    private Integer tenure;

    /**
     * Initial status of the application.
     * Defaults to "PENDING" when set by the service if not explicitly provided.
     */
    private String status;

    /** Raw bytes of the first supporting document (e.g. ID proof). */
    private byte[] docAttachment1;

    /** Raw bytes of the second supporting document (e.g. income proof). */
    private byte[] docAttachment2;

    /** Raw bytes of the third supporting document (e.g. address proof). */
    private byte[] docAttachment3;
}

