package com.spring.tradexportfolioservice.Exception;

import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConcurrentRequestException.class)
    @ResponseStatus(HttpStatus.CONFLICT) // 409 Conflict
    public ApiError handleConcurrentRequest(ConcurrentRequestException ex, HttpServletRequest request) {
        return new ApiError(
                409,
                "CONCURRENT_REQUEST",
                ex.getMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError illegalStateException(IllegalStateException ex, HttpServletRequest request) {
        return new ApiError(
                409,
                "CONFLICT",
                ex.getMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        return new ApiError(
                400,
                "INVALID_REQUEST",
                ex.getMessage(),
                request.getRequestURI());
    }

}
