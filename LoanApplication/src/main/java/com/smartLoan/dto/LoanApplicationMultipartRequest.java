package com.smartLoan.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

/**
 * Multipart/form-data request model used by the create loan application API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplicationMultipartRequest {

    @NotNull(message = "customerId is required")
    @Positive(message = "customerId must be greater than 0")
    private Long customerId;

    @NotBlank(message = "loanType is required")
    private String loanType;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "tenure is required")
    @Positive(message = "tenure must be greater than 0")
    private Integer tenure;

    @Pattern(regexp = "^(?!\\s*$).+", message = "status must not be blank")
    private String status;

    private MultipartFile docAttachment1;

    private MultipartFile docAttachment2;

    private MultipartFile docAttachment3;
}

