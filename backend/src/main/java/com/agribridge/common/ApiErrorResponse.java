package com.agribridge.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard JSON error shape returned by every AgriBridge endpoint, as
 * documented in docs/api-documentation.md:
 *
 * {
 *   "timestamp": "...",
 *   "status": 400,
 *   "error": "VALIDATION_ERROR",
 *   "message": "...",
 *   "details": ["..."]
 * }
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private List<String> details;
}
