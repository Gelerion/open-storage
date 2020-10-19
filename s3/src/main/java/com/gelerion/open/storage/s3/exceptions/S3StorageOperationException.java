package com.gelerion.open.storage.s3.exceptions;

import com.gelerion.open.storage.api.exceptions.StorageOperationException;

public class S3StorageOperationException extends StorageOperationException {

    public S3StorageOperationException(String message) {
        super(message);
    }

    public S3StorageOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
