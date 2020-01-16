package com.gelerion.open.storage.api.exceptions;

public class NotImplementedException extends StorageOperationException {

    public NotImplementedException(String message) {
        super(message);
    }

    public NotImplementedException(String message, Throwable cause) {
        super(message, cause);
    }
}
