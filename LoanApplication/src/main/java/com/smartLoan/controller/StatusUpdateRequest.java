package com.smartLoan.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple DTO for updating the status of a loan application.
 *
 * Used in the PUT {@code /api/v1/loan-applications/{id}/status} endpoint.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdateRequest {

    /**
     * The new status value (e.g. "APPROVED", "REJECTED", "UNDER_REVIEW").
     */
    @NotBlank(message = "status is required")
    private String status;
}

