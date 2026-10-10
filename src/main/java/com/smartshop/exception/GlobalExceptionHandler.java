package com.smartshop.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private Map<String, Object> buildErrorResponse(HttpStatus status, String error, String message, String path) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().format(FORMATTER));
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("path", path);
        return body;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        Map<String, Object> body = buildErrorResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientStock(InsufficientStockException ex, HttpServletRequest request) {
        Map<String, Object> body = buildErrorResponse(HttpStatus.BAD_REQUEST, "Insufficient Stock", ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        if (errorMessage.isEmpty()) {
            errorMessage = "Validation error on request body";
        }
        Map<String, Object> body = buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation Error", errorMessage, request.getRequestURI());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        Map<String, Object> body = buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
        Map<String, Object> body = buildErrorResponse(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(org.springframework.dao.DataIntegrityViolationException ex, HttpServletRequest request) {
        String msg = "An account with this email address already exists. Please sign in instead.";
        Map<String, Object> body = buildErrorResponse(HttpStatus.CONFLICT, "Conflict", msg, request.getRequestURI());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDataAccessException(org.springframework.dao.DataAccessException ex, HttpServletRequest request) {
        String msg = "Database connection unavailable. Please verify your Aiven Cloud MySQL database connection and credentials.";
        Map<String, Object> body = buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, "Database Error", msg, request.getRequestURI());
        return new ResponseEntity<>(body, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        String msg = (ex.getMessage() != null && !ex.getMessage().isBlank()) ? ex.getMessage() : "Incorrect email or password";
        Map<String, Object> body = buildErrorResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", msg, request.getRequestURI());
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        Map<String, Object> body = buildErrorResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        Map<String, Object> body = buildErrorResponse(HttpStatus.FORBIDDEN, "Forbidden", "Access denied: " + ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex, HttpServletRequest request) {
        Map<String, Object> body = buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred", request.getRequestURI());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
