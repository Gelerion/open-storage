package com.gelerion.open.storage.s3.invoker;


import com.amazonaws.services.s3.model.AmazonS3Exception;
import net.jodah.failsafe.RetryPolicy;
import net.jodah.failsafe.event.ExecutionAttemptedEvent;

import java.util.function.Consumer;

public class RetryOnMissingBucketPolicy {

    public static <T> RetryPolicy<T> withCreateBucketFn(Consumer<ExecutionAttemptedEvent<T>> func) {
        return new RetryPolicy<T>()
                .handleIf(ex -> {
                    if (ex instanceof AmazonS3Exception) {
                        //TODO: log
                        return ex.getMessage().contains("bucket does not exist");
                    }
                    return false;
                })
                .withMaxRetries(1)
                .onFailedAttempt(func::accept);
    }
}
