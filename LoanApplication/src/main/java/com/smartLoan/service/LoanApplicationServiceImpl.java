package com.smartLoan.service;

import com.smartLoan.dto.LoanApplicationRequest;
import com.smartLoan.entity.LoanApplication;
import com.smartLoan.exception.DataConflictException;
import com.smartLoan.exception.DatabaseOperationException;
import com.smartLoan.exception.DatabaseUnavailableException;
import com.smartLoan.repository.LoanApplicationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;

/**
 * Default implementation of {@link LoanApplicationService}.
 *
 * <ul>
 *   <li>All write operations are wrapped in a transaction via {@code @Transactional}.</li>
 *   <li>Read operations use {@code @Transactional(readOnly = true)} for performance.</li>
 *   <li>{@code @RequiredArgsConstructor} (Lombok) injects the repository via constructor
 *       injection — the recommended Spring best-practice over field injection.</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoanApplicationServiceImpl implements LoanApplicationService {

    private final LoanApplicationRepository loanApplicationRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Maps every field from the incoming {@link LoanApplicationRequest} DTO onto a
     * new {@link LoanApplication} entity, sets the default status to "PENDING" when
     * none is supplied, and persists it via the repository.
     */
    @Override
    @Transactional
    public LoanApplication createLoanApplication(LoanApplicationRequest request) {

        log.info("Creating loan application for customerId={}, loanType={}",
                request.getCustomerId(), request.getLoanType());

        LoanApplication loanApplication = LoanApplication.builder()
                .customerId(request.getCustomerId())
                .loanType(request.getLoanType())
                .amount(request.getAmount())
                .tenure(request.getTenure())
                // Default status to PENDING if caller does not specify one
                .status(request.getStatus() != null ? request.getStatus() : "PENDING")
                .docAttachment1(request.getDocAttachment1())
                .docAttachment2(request.getDocAttachment2())
                .docAttachment3(request.getDocAttachment3())
                .build();

        LoanApplication saved = executeRepositoryOperation(
                "create the loan application",
                () -> loanApplicationRepository.save(loanApplication));

        log.info("Loan application persisted successfully with applicationId={}",
                saved.getApplicationId());

        return saved;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // READ
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public LoanApplication getLoanApplicationById(Long applicationId) {

        log.info("Fetching loan application with applicationId={}", applicationId);

        return executeRepositoryOperation(
                "fetch the loan application",
                () -> loanApplicationRepository.findById(applicationId)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Loan application not found for applicationId: " + applicationId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanApplication> getLoanApplicationsByCustomerId(Long customerId) {

        log.info("Fetching all loan applications for customerId={}", customerId);
        return executeRepositoryOperation(
                "fetch loan applications for customerId " + customerId,
                () -> loanApplicationRepository.findByCustomerId(customerId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanApplication> getLoanApplicationsByStatus(String status) {

        log.info("Fetching all loan applications with status={}", status);
        return executeRepositoryOperation(
                "fetch loan applications for status " + status,
                () -> loanApplicationRepository.findByStatus(status));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UPDATE
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public LoanApplication updateLoanApplicationStatus(Long applicationId, String newStatus) {

        log.info("Updating status of applicationId={} to '{}'", applicationId, newStatus);

        LoanApplication existing = executeRepositoryOperation(
                "fetch the loan application for update",
                () -> loanApplicationRepository.findById(applicationId)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Loan application not found for applicationId: " + applicationId)));

        existing.setStatus(newStatus);
        LoanApplication updated = executeRepositoryOperation(
                "update the loan application status",
                () -> loanApplicationRepository.save(existing));

        log.info("Status updated successfully for applicationId={}", applicationId);
        return updated;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteLoanApplication(Long applicationId) {

        log.info("Deleting loan application with applicationId={}", applicationId);

        boolean exists = executeRepositoryOperation(
                "verify the loan application before deletion",
                () -> loanApplicationRepository.existsById(applicationId));

        if (!exists) {
            throw new EntityNotFoundException(
                    "Loan application not found for applicationId: " + applicationId);
        }

        executeRepositoryAction(
                "delete the loan application",
                () -> loanApplicationRepository.deleteById(applicationId));
        log.info("Loan application deleted successfully for applicationId={}", applicationId);
    }

    private <T> T executeRepositoryOperation(String operation, Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (DataIntegrityViolationException exception) {
            log.error("Repository operation failed due to data conflict while trying to {}", operation, exception);
            throw new DataConflictException(
                    "Unable to " + operation + " because the request conflicts with database constraints.",
                    exception);
        } catch (DataAccessResourceFailureException | QueryTimeoutException | CannotAcquireLockException exception) {
            log.error("Repository operation failed because the database is unavailable while trying to {}: {}",
                    operation, exception.getMessage(), exception);
            throw new DatabaseUnavailableException(
                    "Unable to " + operation + " because the database is currently unavailable. Please try again later.",
                    exception);
        } catch (DataAccessException exception) {
            log.error("Repository operation failed due to a database error while trying to {}", operation, exception);
            throw new DatabaseOperationException(
                    "Unable to " + operation + " due to a database error. Please contact support if the issue persists.",
                    exception);
        }
    }

    private void executeRepositoryAction(String operation, Runnable action) {
        executeRepositoryOperation(operation, () -> {
            action.run();
            return null;
        });
    }
}

