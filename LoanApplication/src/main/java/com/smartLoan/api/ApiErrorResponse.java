package com.smartLoan.api;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * Standard error payload returned to API consumers for handled failures.
 */
@Value
@Builder
public class ApiErrorResponse {
    LocalDateTime timestamp;
    int status;
    String error;
    String message;
    String path;
}

