package com.spring.tradexportfolioservice.Exception;

public class ConcurrentRequestException extends RuntimeException {
    public ConcurrentRequestException(String message) {
        super(message);
    }
}
