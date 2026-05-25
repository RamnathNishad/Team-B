package com.smartLoan.exception;

/**
 * Raised when the database is unavailable, unreachable, or timed out.
 */
public class DatabaseUnavailableException extends RuntimeException {

    public DatabaseUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}

