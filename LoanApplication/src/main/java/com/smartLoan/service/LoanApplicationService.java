package com.smartLoan.service;

import com.smartLoan.dto.LoanApplicationRequest;
import com.smartLoan.entity.LoanApplication;

import java.util.List;

/**
 * Service contract for Loan Application business operations.
 *
 * Keeping an interface separate from its implementation allows easy unit-testing
 * via mocking and makes it straightforward to swap implementations in the future.
 */
public interface LoanApplicationService {

    /**
     * Create and persist a new loan application.
     *
     * @param request DTO carrying all loan application fields (including document bytes)
     * @return the saved {@link LoanApplication} entity (with generated application_id)
     */
    LoanApplication createLoanApplication(LoanApplicationRequest request);

    /**
     * Retrieve a loan application by its primary key.
     *
     * @param applicationId the auto-generated application ID
     * @return the matching {@link LoanApplication}
     * @throws jakarta.persistence.EntityNotFoundException if no record found
     */
    LoanApplication getLoanApplicationById(Long applicationId);

    /**
     * Retrieve all loan applications submitted by a specific customer.
     *
     * @param customerId the customer's ID
     * @return list of matching loan applications (may be empty)
     */
    List<LoanApplication> getLoanApplicationsByCustomerId(Long customerId);

    /**
     * Retrieve all loan applications filtered by status.
     *
     * @param status e.g. "PENDING", "APPROVED", "REJECTED"
     * @return list of matching loan applications (may be empty)
     */
    List<LoanApplication> getLoanApplicationsByStatus(String status);

    /**
     * Update the status of an existing loan application.
     *
     * @param applicationId the application to update
     * @param newStatus     the new status string
     * @return the updated {@link LoanApplication}
     */
    LoanApplication updateLoanApplicationStatus(Long applicationId, String newStatus);

    /**
     * Delete a loan application by its primary key.
     *
     * @param applicationId the application to delete
     */
    void deleteLoanApplication(Long applicationId);
}

