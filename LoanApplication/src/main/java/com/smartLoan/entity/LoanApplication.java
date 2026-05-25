package com.smartLoan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * JPA Entity representing a Loan Application.
 *
 * Document attachments (doc_attachment1, doc_attachment2, doc_attachment3) are
 * stored as raw binary data (LONGBLOB) directly in the MySQL database.
 */
@Entity
@Table(name = "loan_application")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplication {

    /** Primary key – auto-incremented by the database. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long applicationId;

    /** Foreign-key reference to the customer who submitted the application. */
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    /**
     * Type of loan (e.g. HOME, PERSONAL, VEHICLE, EDUCATION).
     * Stored as a VARCHAR so future types can be added without altering the schema.
     */
    @Column(name = "loan_type", nullable = false, length = 50)
    private String loanType;

    /** Requested loan amount. Precision 15, scale 2 covers up to 999 trillion. */
    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    /** Loan tenure expressed in months. */
    @Column(name = "tenure", nullable = false)
    private Integer tenure;

    /**
     * Current status of the application
     * (e.g. PENDING, UNDER_REVIEW, APPROVED, REJECTED, DISBURSED).
     */
    @Column(name = "status", nullable = false, length = 30)
    private String status;

    // -------------------------------------------------------------------------
    // Document Attachments
    // Each attachment stores the raw file bytes as a LONGBLOB in MySQL.
    // @Lob instructs JPA to treat the field as a large object.
    // columnDefinition = "LONGBLOB" ensures MySQL uses the correct type
    // (supports files up to ~4 GB).
    // -------------------------------------------------------------------------

    /** First supporting document (e.g. ID proof). */
    @Lob
    @Column(name = "doc_attachment1", columnDefinition = "LONGBLOB")
    private byte[] docAttachment1;

    /** Second supporting document (e.g. income proof). */
    @Lob
    @Column(name = "doc_attachment2", columnDefinition = "LONGBLOB")
    private byte[] docAttachment2;

    /** Third supporting document (e.g. address proof). */
    @Lob
    @Column(name = "doc_attachment3", columnDefinition = "LONGBLOB")
    private byte[] docAttachment3;
}

