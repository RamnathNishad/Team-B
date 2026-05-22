package com.smartLoan.validation;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;
import java.util.Set;

/**
 * Central validation layer for incoming loan-application requests.
 */
@Component
public class LoanApplicationValidator {

    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024;

    private static final Set<String> ALLOWED_LOAN_TYPES = Set.of("personal", "home", "auto");
    private static final Set<String> ALLOWED_FILE_EXTENSIONS = Set.of("pdf", "jpg", "jpeg", "png");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png"
    );

    /**
     * Validates the create-loan request fields required by the controller.
     */
    public void validateCreateLoanRequest(
            String loanType,
            MultipartFile docAttachment1,
            MultipartFile docAttachment2,
            MultipartFile docAttachment3) {

        validateLoanType(loanType);
        validateDocument("docAttachment1", docAttachment1);
        validateDocument("docAttachment2", docAttachment2);
        validateDocument("docAttachment3", docAttachment3);
    }

    /**
     * Returns the canonical persisted value for a valid loan type.
     */
    public String normalizeLoanType(String loanType) {
        String normalized = normalizeRequiredText(loanType, "loanType").toLowerCase(Locale.ROOT);

        return switch (normalized) {
            case "personal" -> "Personal";
            case "home" -> "Home";
            case "auto" -> "Auto";
            default -> throw new LoanApplicationValidationException(
                    "Invalid loanType. Supported values are: Personal, Home, Auto");
        };
    }

    private void validateLoanType(String loanType) {
        String normalized = normalizeRequiredText(loanType, "loanType").toLowerCase(Locale.ROOT);
        if (!ALLOWED_LOAN_TYPES.contains(normalized)) {
            throw new LoanApplicationValidationException(
                    "Invalid loanType. Supported values are: Personal, Home, Auto");
        }
    }

    private void validateDocument(String fieldName, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return;
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new LoanApplicationValidationException(
                    fieldName + " exceeds maximum allowed size of 5MB");
        }

        String extension = extractExtension(file.getOriginalFilename());
        String contentType = file.getContentType() == null
                ? ""
                : file.getContentType().trim().toLowerCase(Locale.ROOT);

        boolean validExtension = ALLOWED_FILE_EXTENSIONS.contains(extension);
        boolean validContentType = contentType.isEmpty() || ALLOWED_CONTENT_TYPES.contains(contentType);

        if (!validExtension || !validContentType) {
            throw new LoanApplicationValidationException(
                    fieldName + " must be one of the supported file types: PDF, JPG, PNG");
        }
    }

    private String extractExtension(String originalFilename) {
        String filename = normalizeRequiredText(originalFilename, "document filename");
        int lastDotIndex = filename.lastIndexOf('.');

        if (lastDotIndex < 0 || lastDotIndex == filename.length() - 1) {
            throw new LoanApplicationValidationException(
                    "Uploaded documents must have a valid extension: PDF, JPG, PNG");
        }

        return filename.substring(lastDotIndex + 1).trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeRequiredText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new LoanApplicationValidationException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}

