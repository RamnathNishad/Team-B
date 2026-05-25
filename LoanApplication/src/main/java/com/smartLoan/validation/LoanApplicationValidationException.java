package com.smartLoan.validation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Raised when incoming loan application data violates validation rules.
 *
 * Annotated with {@link ResponseStatus} so controller requests automatically
 * receive HTTP 400 without additional exception-mapping boilerplate.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LoanApplicationValidationException extends RuntimeException {

    public LoanApplicationValidationException(String message) {
        super(message);
    }
}

