package com.smartLoan.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoanApplicationValidatorTest {

    private LoanApplicationValidator loanApplicationValidator;

    @BeforeEach
    void setUp() {
        loanApplicationValidator = new LoanApplicationValidator();
    }

    @Test
    void shouldAcceptSupportedPdfJpgAndPngFiles() {
        MockMultipartFile pdf = new MockMultipartFile(
                "docAttachment1",
                "identity-proof.pdf",
                "application/pdf",
                "pdf-content".getBytes());
        MockMultipartFile jpg = new MockMultipartFile(
                "docAttachment2",
                "photo.jpg",
                "image/jpeg",
                "jpg-content".getBytes());
        MockMultipartFile png = new MockMultipartFile(
                "docAttachment3",
                "address.png",
                "image/png",
                "png-content".getBytes());

        assertDoesNotThrow(() -> loanApplicationValidator.validateCreateLoanRequest("home", pdf, jpg, png));
    }

    @Test
    void shouldRejectUnsupportedFileExtension() {
        MockMultipartFile txt = new MockMultipartFile(
                "docAttachment1",
                "notes.txt",
                "text/plain",
                "not-supported".getBytes());

        LoanApplicationValidationException exception = assertThrows(
                LoanApplicationValidationException.class,
                () -> loanApplicationValidator.validateCreateLoanRequest("Personal", txt, null, null));

        assertEquals("docAttachment1 must be one of the supported file types: PDF, JPG, PNG", exception.getMessage());
    }

    @Test
    void shouldRejectOversizedFile() {
        byte[] oversizedContent = new byte[5 * 1024 * 1024 + 1];
        MockMultipartFile largePdf = new MockMultipartFile(
                "docAttachment1",
                "income-proof.pdf",
                "application/pdf",
                oversizedContent);

        LoanApplicationValidationException exception = assertThrows(
                LoanApplicationValidationException.class,
                () -> loanApplicationValidator.validateCreateLoanRequest("Auto", largePdf, null, null));

        assertEquals("docAttachment1 exceeds maximum allowed size of 5MB", exception.getMessage());
    }

    @Test
    void shouldAcceptLoanTypeIgnoringCaseAndWhitespace() {
        assertDoesNotThrow(() -> loanApplicationValidator.validateCreateLoanRequest("  pErSoNaL  ", null, null, null));
        assertEquals("Personal", loanApplicationValidator.normalizeLoanType("  pErSoNaL  "));
        assertEquals("Home", loanApplicationValidator.normalizeLoanType("home"));
        assertEquals("Auto", loanApplicationValidator.normalizeLoanType("AUTO"));
    }

    @Test
    void shouldRejectUnsupportedLoanType() {
        LoanApplicationValidationException exception = assertThrows(
                LoanApplicationValidationException.class,
                () -> loanApplicationValidator.validateCreateLoanRequest("Education", null, null, null));

        assertEquals("Invalid loanType. Supported values are: Personal, Home, Auto", exception.getMessage());
    }

    @Test
    void shouldRejectBlankLoanType() {
        LoanApplicationValidationException exception = assertThrows(
                LoanApplicationValidationException.class,
                () -> loanApplicationValidator.validateCreateLoanRequest("   ", null, null, null));

        assertEquals("loanType must not be blank", exception.getMessage());
    }

    @Test
    void shouldAllowMissingOptionalAttachments() {
        assertDoesNotThrow(() -> loanApplicationValidator.validateCreateLoanRequest("Home", null, null, null));
    }
}

