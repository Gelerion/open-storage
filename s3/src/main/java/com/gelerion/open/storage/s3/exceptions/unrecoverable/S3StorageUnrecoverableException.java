package com.gelerion.open.storage.s3.exceptions.unrecoverable;

import com.gelerion.open.storage.s3.exceptions.S3StorageOperationException;

public class S3StorageUnrecoverableException extends S3StorageOperationException {

    public S3StorageUnrecoverableException(String message) {
        super(message);
    }

    public S3StorageUnrecoverableException(String message, Throwable cause) {
        super(message, cause);
    }
}
