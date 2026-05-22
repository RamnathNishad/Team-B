package com.smartLoan.exception;

/**
 * Raised when a database constraint or integrity rule is violated.
 */
public class DataConflictException extends RuntimeException {

    public DataConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}

