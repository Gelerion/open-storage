package com.gelerion.open.storage.s3.invoker.translator;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.gelerion.open.storage.s3.exceptions.S3StorageOperationException;

public class DefaultAwsExceptionsTranslator implements AwsExceptionsTranslator {

    @Override
    public Throwable translate(Throwable exception) {
        //return as is
        if (!(exception instanceof AmazonClientException)) {
            return exception;
        }

        if (exception instanceof AmazonS3Exception) {
            S3StorageOperationException stam = new S3StorageOperationException("stam");
            stam.addSuppressed(exception);
            return stam;
        }


        return exception;
    }
}
