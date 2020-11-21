package com.gelerion.open.storage.s3.exceptions.recoverable;

import com.amazonaws.AmazonServiceException;

public class S3StorageConnectionTimeoutException extends S3StorageRecoverableException {
    public S3StorageConnectionTimeoutException(String message) {
        super(message);
    }

    public S3StorageConnectionTimeoutException(String message, AmazonServiceException cause) {
        super(message, cause);
    }
}
