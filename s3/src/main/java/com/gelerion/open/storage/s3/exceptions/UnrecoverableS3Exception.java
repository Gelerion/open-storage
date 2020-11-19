package com.gelerion.open.storage.s3.exceptions;

public class UnrecoverableS3Exception extends S3StorageOperationException {

    public UnrecoverableS3Exception(String message) {
        super(message);
    }

    public UnrecoverableS3Exception(String message, Throwable cause) {
        super(message, cause);
    }
}
