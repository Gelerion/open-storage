package com.gelerion.open.storage.s3.exceptions.unrecoverable;

public class S3StorageBucketDoesNotExistException extends S3StorageUnrecoverableException {

    public S3StorageBucketDoesNotExistException(String message) {
        super(message);
    }

    public S3StorageBucketDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
