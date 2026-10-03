package com.agribridge.exception;

import com.agribridge.common.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Central exception handler ensuring every error response, across every
 * module, follows the same JSON shape (ApiErrorResponse) rather than each
 * controller formatting errors independently. This also prevents internal
 * details (stack traces, raw exception messages) from leaking to clients.
 *
 * WEEK 4 FIX (BUG-001): added an explicit handler for Spring Security's
 * AccessDeniedException, thrown by @PreAuthorize when an authenticated user
 * lacks the required role (e.g. a BUYER calling a FARMER/ADMIN-only
 * endpoint). Previously this exception had no dedicated handler here, so it
 * fell through to handleGeneric() below and was incorrectly reported as
 * HTTP 500 Internal Server Error instead of HTTP 403 Forbidden - see
 * BUG_REGISTER.md (BUG-001) and DEBUGGING_LOG.md for the full root-cause
 * analysis, reproduction steps, and verification
 * (GlobalExceptionHandlerTest.handleAccessDenied_returns403).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), null);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicate(DuplicateResourceException ex) {
        return build(HttpStatus.CONFLICT, "CONFLICT", ex.getMessage(), null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return build(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Email or password is incorrect.", null);
    }

    // WEEK 4 FIX (BUG-001): previously missing. Without this handler,
    // a role check failure from @PreAuthorize fell through to
    // handleGeneric() and was returned as HTTP 500 instead of HTTP 403.
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return build(HttpStatus.FORBIDDEN, "ACCESS_DENIED",
                "You do not have permission to perform this action.", null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "One or more fields are invalid.", details);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex) {
        // Deliberately generic: never leak stack traces or internal exception
        // messages to the client. Full details still reach the server logs
        // via Spring Boot's default logging (see docs/architecture.md, Section 12).
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "Something went wrong. Please try again later.", null);
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String error, String message, List<String> details) {
        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .details(details)
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
