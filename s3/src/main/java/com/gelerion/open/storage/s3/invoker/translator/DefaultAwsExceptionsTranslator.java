package com.gelerion.open.storage.s3.invoker.translator;

import com.amazonaws.AbortedException;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkBaseException;
import com.amazonaws.retry.RetryUtils;
import com.gelerion.open.storage.api.exceptions.StorageOperationException;
import com.gelerion.open.storage.s3.exceptions.unrecoverable.S3StorageBucketDoesNotExistException;
import com.gelerion.open.storage.s3.exceptions.unrecoverable.S3StorageUnrecoverableException;
import com.gelerion.open.storage.s3.exceptions.recoverable.*;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;

public class DefaultAwsExceptionsTranslator implements AwsExceptionsTranslator {
    private static final int HTTP_BAD_REQUEST = 400;
    private static final int HTTP_UNAUTHORIZED = 401;
    private static final int HTTP_PAYMENT_REQUIRED = 402;
    private static final int HTTP_FORBIDDEN = 403;
    private static final int HTTP_NOT_FOUND = 404;

    private static final int OUT_OF_RANGE = 416;
    private static final int NO_RESPONSE_FROM_SERVER_443 = 443;
    private static final int NO_RESPONSE_FROM_SERVER_444 = 444;
    private static final int THROTTLE = 503;
    private static final int INTERNAL_ERROR = 500;


    private static final String NO_SUCH_BUCKET = "NoSuchBucket";
    private static final String EOF_MESSAGE_IN_XML_PARSER
            = "Failed to sanitize XML document destined for handler class";


    //We try to decide whether this exception is recoverable or not
    @Override
    public Throwable translate(Throwable exception) {
        if (exception instanceof Error) {
            return exception;
        }

        //get inner -- ??
        if (exception instanceof StorageOperationException) {
            return exception;
        }

        if (!(exception instanceof AmazonClientException)) {
            return new S3StorageUnrecoverableException(exception.getMessage(), exception);
        }

        AmazonServiceException ase = (AmazonServiceException) exception;
        int status = ase.getStatusCode();

        // recoverable
        if (isThrottleException(ase)) {
            return new S3StorageThrottlingException(ase.getMessage(), ase);
        }

        if (isConnectionTimeoutException(ase)) {
            return new S3StorageConnectionTimeoutException(ase.getMessage(), ase);
        }

        if (signifiesConnectionBroken(ase)) { //aka EOFException
            return new S3StorageEofException(ase.getMessage(), ase);
        }

        // status 500 error code is also treated as a connectivity problem
        if (isInternalError(ase)) { //aka AWSStatus500Exception
            return new S3StorageStatus500Exception(ase.getMessage(), ase);
        }

        // server didn't respond.
        if (isNoResponseException(ase)) { // aka AWSNoResponseException
            return new S3StorageNoResponseException(ase.getMessage(), ase);
        }

        // Unrecoverable exceptions

        //special case
        if (isUnknownBucket(ase)) {
            return new S3StorageBucketDoesNotExistException(ase.getMessage(), ase);
        }

        switch (status) {
            case HTTP_BAD_REQUEST:
            case HTTP_UNAUTHORIZED:
            case HTTP_PAYMENT_REQUIRED:
            case HTTP_FORBIDDEN:
            case HTTP_NOT_FOUND:
                return new S3StorageUnrecoverableException(exception.getMessage(), exception);
        }

        return new S3StorageUnrecoverableException(exception.getMessage(), exception);
    }

    public static boolean isUnknownBucket(AmazonServiceException e) {
        return e.getStatusCode() == HTTP_NOT_FOUND
                && NO_SUCH_BUCKET.equals(e.getErrorCode());
    }

    public static boolean signifiesConnectionBroken(AmazonServiceException ase) {
        return OUT_OF_RANGE == ase.getStatusCode() || ase.toString().contains(EOF_MESSAGE_IN_XML_PARSER);
    }

    private static boolean isThrottleException(AmazonServiceException ase) {
        return THROTTLE == ase.getStatusCode() || RetryUtils.isThrottlingException(ase);
    }

    private static boolean isInternalError(AmazonServiceException ase) {
        return INTERNAL_ERROR == ase.getStatusCode();
    }

    private static boolean isNoResponseException(AmazonServiceException ase) {
        return NO_RESPONSE_FROM_SERVER_443 == ase.getStatusCode() || NO_RESPONSE_FROM_SERVER_444 == ase.getStatusCode();
    }

    @SuppressWarnings("ThrowableInstanceNeverThrown")
    private static boolean isConnectionTimeoutException(SdkBaseException ex) {
        Exception innerCause = containsInterruptedException(ex);
        if (innerCause != null) {
            // interrupted IO, or a socket exception underneath that class
            return signifiesConnectionTimeoutException(innerCause);
        }
        return false;
    }

    private static Exception containsInterruptedException(Throwable thrown) {
        if (thrown == null) {
            return null;
        }

        if (thrown instanceof InterruptedException ||
            thrown instanceof InterruptedIOException ||
            thrown instanceof AbortedException) {
            return (Exception)thrown;
        }
        // tail recurse
        return containsInterruptedException(thrown.getCause());
    }

    private static boolean signifiesConnectionTimeoutException(final Exception innerCause) {
        if (innerCause instanceof SocketTimeoutException) {
            return true;
        }

        if (innerCause instanceof ConnectTimeoutException) {
            return true;
        }

        String name = innerCause.getClass().getName();
        // TCP connection http timeout from the shaded or unshaded filenames
        // com.amazonaws.thirdparty.apache.http.conn.ConnectTimeoutException
        return name.endsWith(".ConnectTimeoutException") || name.endsWith("$ConnectTimeoutException");
    }
}