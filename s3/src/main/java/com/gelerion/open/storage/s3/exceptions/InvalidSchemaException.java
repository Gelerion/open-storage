package com.gelerion.open.storage.s3.exceptions;

public class InvalidSchemaException extends S3StorageOperationException {


    public InvalidSchemaException(String path) {
        super(String.format("Invalid schema provided in %s. The path must start with 's3[a|n]://'", path));
    }
}
