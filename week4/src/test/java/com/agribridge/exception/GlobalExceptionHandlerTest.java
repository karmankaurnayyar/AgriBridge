package com.agribridge.exception;

import com.agribridge.common.ApiErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * WEEK 4 — Unit tests for GlobalExceptionHandler.
 *
 * GlobalExceptionHandler has no constructor dependencies, so every handler
 * method can be exercised as a plain, isolated unit test with no Spring
 * context, no mocking, and no database - these tests instantiate the class
 * directly and call each @ExceptionHandler method as an ordinary Java
 * method.
 *
 * WK4-EXC-05 (handleAccessDenied_returns403) is the test that specifically
 * verifies the BUG-001 fix: before the fix, AccessDeniedException had no
 * dedicated handler here and fell through to handleGeneric(), which this
 * same test would have shown returning 500 instead of 403. See
 * BUG_REGISTER.md and DEBUGGING_LOG.md for the full root-cause write-up.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("WK4-EXC-01: ResourceNotFoundException maps to 404 NOT_FOUND")
    void handleNotFound_returns404() {
        ResponseEntity<ApiErrorResponse> response = handler.handleNotFound(new ResourceNotFoundException("Farm not found: 99"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("NOT_FOUND");
        assertThat(response.getBody().getMessage()).contains("Farm not found");
    }

    @Test
    @DisplayName("WK4-EXC-02: DuplicateResourceException maps to 409 CONFLICT")
    void handleDuplicate_returns409() {
        ResponseEntity<ApiErrorResponse> response = handler.handleDuplicate(new DuplicateResourceException("Email already registered"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getError()).isEqualTo("CONFLICT");
    }

    @Test
    @DisplayName("WK4-EXC-03: BadCredentialsException maps to 401 UNAUTHORIZED with a generic message")
    void handleBadCredentials_returns401() {
        ResponseEntity<ApiErrorResponse> response = handler.handleBadCredentials(new BadCredentialsException("ignored - message is generic by design"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getMessage()).isEqualTo("Email or password is incorrect.");
    }

    @Test
    @DisplayName("WK4-EXC-04: IllegalArgumentException maps to 400 BAD_REQUEST")
    void handleIllegalArgument_returns400() {
        ResponseEntity<ApiErrorResponse> response = handler.handleIllegalArgument(
                new IllegalArgumentException("Expected harvest date cannot be before the sowing date."));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getError()).isEqualTo("BAD_REQUEST");
    }

    @Test
    @DisplayName("WK4-EXC-05 (BUG-001 fix verification): AccessDeniedException maps to 403 FORBIDDEN, not 500")
    void handleAccessDenied_returns403() {
        ResponseEntity<ApiErrorResponse> response = handler.handleAccessDenied(
                new AccessDeniedException("Access is denied"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getError()).isEqualTo("ACCESS_DENIED");
        assertThat(response.getBody().getMessage()).doesNotContain("Something went wrong");
    }

    @Test
    @DisplayName("WK4-EXC-06: an unrecognized exception type falls back to 500 with a generic, non-leaking message")
    void handleGeneric_returns500_andDoesNotLeakDetails() {
        ResponseEntity<ApiErrorResponse> response = handler.handleGeneric(
                new RuntimeException("raw internal detail that must never reach the client"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getMessage()).doesNotContain("raw internal detail");
    }

    @Test
    @DisplayName("WK4-EXC-07: every error response includes a non-null timestamp")
    void everyErrorResponse_includesTimestamp() {
        ResponseEntity<ApiErrorResponse> response = handler.handleNotFound(new ResourceNotFoundException("x"));

        assertThat(response.getBody().getTimestamp()).isNotNull();
    }
}
