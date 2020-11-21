package com.gelerion.open.storage.s3.exceptions.recoverable;

import com.amazonaws.AmazonServiceException;

public class S3StorageEofException extends S3StorageRecoverableException {
    public S3StorageEofException(String message) {
        super(message);
    }

    public S3StorageEofException(String message, AmazonServiceException cause) {
        super(message, cause);
    }
}
