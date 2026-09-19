package com.agribridge.exception;

/** Thrown when a requested entity (e.g., a Farm by ID) does not exist or is not owned by the caller. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
