package com.gelerion.open.storage.s3.exceptions;

public class S3BucketMustBeProvidedException extends S3StorageOperationException {

    public S3BucketMustBeProvidedException(String path) {
        super(String.format("Bucket must be provided in %s", path));
    }

    public S3BucketMustBeProvidedException(String path, Throwable cause) {
        super(String.format("Bucket must be provided in %s", path), cause);
    }

}
