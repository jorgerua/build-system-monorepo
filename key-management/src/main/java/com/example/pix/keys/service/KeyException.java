package com.example.pix.keys.service;

/** Runtime exception carrying a structured error code prefix (e.g. KEY_NOT_FOUND, KEY_ALREADY_EXISTS). */
public class KeyException extends RuntimeException {
    public KeyException(String message) {
        super(message);
    }
}
