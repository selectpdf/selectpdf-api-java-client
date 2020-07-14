package com.selectpdf;

/**
 * Exception thrown by SelectPdf API Client.
 */
public class ApiException extends RuntimeException {
    /**
     * Constructor for ApiException.
     */
    public ApiException() {}
    
    /**
     * 
     * @param message The exception message.
     */
    public ApiException(String message) {
        super(message);
    }

    /**
     * Constructor for ApiException.
     * @param throwable Inner exception.
     */
    public ApiException(Throwable throwable) { 
        super(throwable); 
    }
}