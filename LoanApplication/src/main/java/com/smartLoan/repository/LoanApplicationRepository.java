package com.smartLoan.repository;

import com.smartLoan.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository layer for {@link LoanApplication}.
 *
 * Extending {@link JpaRepository} gives us full CRUD + pagination out-of-the-box:
 *  - save(entity)          → INSERT / UPDATE
 *  - findById(id)          → SELECT by PK
 *  - findAll()             → SELECT all
 *  - deleteById(id)        → DELETE by PK
 *  - count()               → SELECT COUNT(*)
 *  ... and many more.
 *
 * Custom derived query methods are declared below and Spring Data JPA
 * automatically generates the SQL at startup — no implementation needed.
 */
@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {

    /**
     * Fetch all loan applications submitted by a specific customer.
     *
     * @param customerId the customer's ID
     * @return list of matching loan applications
     */
    List<LoanApplication> findByCustomerId(Long customerId);

    /**
     * Fetch all loan applications that have a particular status
     * (e.g. "PENDING", "APPROVED", "REJECTED").
     *
     * @param status the application status
     * @return list of matching loan applications
     */
    List<LoanApplication> findByStatus(String status);

    /**
     * Fetch all loan applications of a specific loan type
     * (e.g. "HOME", "PERSONAL", "VEHICLE").
     *
     * @param loanType the loan type
     * @return list of matching loan applications
     */
    List<LoanApplication> findByLoanType(String loanType);

    /**
     * Fetch all loan applications for a customer filtered by loan type.
     *
     * @param customerId the customer's ID
     * @param loanType   the loan type
     * @return list of matching loan applications
     */
    List<LoanApplication> findByCustomerIdAndLoanType(Long customerId, String loanType);

    /**
     * Fetch all loan applications for a customer filtered by status.
     *
     * @param customerId the customer's ID
     * @param status     the application status
     * @return list of matching loan applications
     */
    List<LoanApplication> findByCustomerIdAndStatus(Long customerId, String status);

    /**
     * Check whether a loan application exists for a given customer and status.
     *
     * @param customerId the customer's ID
     * @param status     the application status
     * @return true if at least one matching record exists
     */
    boolean existsByCustomerIdAndStatus(Long customerId, String status);
}

