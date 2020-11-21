package com.gelerion.open.storage.s3.exceptions.recoverable;

import com.amazonaws.AmazonServiceException;
import com.gelerion.open.storage.s3.exceptions.S3StorageOperationException;

public class S3StorageRecoverableException extends S3StorageOperationException {
    public S3StorageRecoverableException(String message) {
        super(message);
    }

    public S3StorageRecoverableException(String message, AmazonServiceException cause) {
        super(message, cause);
    }
}
