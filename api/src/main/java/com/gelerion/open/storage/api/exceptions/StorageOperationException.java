package com.gelerion.open.storage.api.exceptions;

public class StorageOperationException extends RuntimeException {
    private final String reason;

    public StorageOperationException(String message) {
        super(message);
        this.reason = message;
    }

    public StorageOperationException(String message, Throwable cause) {
        super(message, cause);
        this.reason = message;
    }

    public String reason() {
        return reason;
    }
}
