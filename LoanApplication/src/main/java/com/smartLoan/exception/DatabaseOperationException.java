package com.smartLoan.exception;

/**
 * Raised when a generic database operation fails in the persistence layer.
 */
public class DatabaseOperationException extends RuntimeException {

    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}

