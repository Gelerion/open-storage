package com.gelerion.open.storage.s3.exceptions.recoverable;

import com.amazonaws.AmazonServiceException;

public class S3StorageThrottlingException extends S3StorageRecoverableException {
    public S3StorageThrottlingException(String message) {
        super(message);
    }

    public S3StorageThrottlingException(String message, AmazonServiceException cause) {
        super(message, cause);
    }
}
