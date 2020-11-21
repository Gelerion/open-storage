package com.gelerion.open.storage.s3.exceptions.recoverable;

import com.amazonaws.AmazonServiceException;

public class S3StorageStatus500Exception extends S3StorageRecoverableException {
    public S3StorageStatus500Exception(String message) {
        super(message);
    }

    public S3StorageStatus500Exception(String message, AmazonServiceException cause) {
        super(message, cause);
    }
}
