package com.smartLoan.service;

import com.smartLoan.dto.LoanApplicationRequest;
import com.smartLoan.entity.LoanApplication;
import com.smartLoan.exception.DataConflictException;
import com.smartLoan.exception.DatabaseOperationException;
import com.smartLoan.exception.DatabaseUnavailableException;
import com.smartLoan.repository.LoanApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanApplicationServiceImplTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    private LoanApplicationServiceImpl loanApplicationService;

    @BeforeEach
    void setUp() {
        loanApplicationService = new LoanApplicationServiceImpl(loanApplicationRepository);
    }

    @Test
    void shouldTranslateConstraintViolationToConflictException() {
        when(loanApplicationRepository.save(any(LoanApplication.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key"));

        DataConflictException exception = assertThrows(
                DataConflictException.class,
                () -> loanApplicationService.createLoanApplication(buildRequest()));

        assertEquals(
                "Unable to create the loan application because the request conflicts with database constraints.",
                exception.getMessage());
    }

    @Test
    void shouldTranslateDatabaseUnavailabilityToServiceUnavailableException() {
        when(loanApplicationRepository.findByCustomerId(101L))
                .thenThrow(new DataAccessResourceFailureException("database down"));

        DatabaseUnavailableException exception = assertThrows(
                DatabaseUnavailableException.class,
                () -> loanApplicationService.getLoanApplicationsByCustomerId(101L));

        assertEquals(
                "Unable to fetch loan applications for customerId 101 because the database is currently unavailable. Please try again later.",
                exception.getMessage());
    }

    @Test
    void shouldTranslateGenericDataAccessFailureToDatabaseOperationException() {
        when(loanApplicationRepository.findByStatus("PENDING"))
                .thenThrow(new InvalidDataAccessResourceUsageException("bad sql"));

        DatabaseOperationException exception = assertThrows(
                DatabaseOperationException.class,
                () -> loanApplicationService.getLoanApplicationsByStatus("PENDING"));

        assertEquals(
                "Unable to fetch loan applications for status PENDING due to a database error. Please contact support if the issue persists.",
                exception.getMessage());
    }

    private LoanApplicationRequest buildRequest() {
        return LoanApplicationRequest.builder()
                .customerId(1L)
                .loanType("Home")
                .amount(BigDecimal.valueOf(500000))
                .tenure(60)
                .status("PENDING")
                .build();
    }
}

