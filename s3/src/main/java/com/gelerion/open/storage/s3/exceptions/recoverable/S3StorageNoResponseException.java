package com.gelerion.open.storage.s3.exceptions.recoverable;

import com.amazonaws.AmazonServiceException;

public class S3StorageNoResponseException extends S3StorageRecoverableException {
    public S3StorageNoResponseException(String message) {
        super(message);
    }

    public S3StorageNoResponseException(String message, AmazonServiceException cause) {
        super(message, cause);
    }
}
