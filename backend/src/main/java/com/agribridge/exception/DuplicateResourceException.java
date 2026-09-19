package com.agribridge.exception;

/** Thrown when a uniqueness constraint would be violated (e.g., registering an email that already exists). */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
