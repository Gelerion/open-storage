package com.gelerion.open.storage.s3.invoker;

import com.gelerion.open.storage.api.domain.StoragePath;
import com.gelerion.open.storage.api.ops.StorageOperations.FunctionExceptional;
import com.gelerion.open.storage.s3.S3Storage;
import com.gelerion.open.storage.s3.invoker.retry.RetryDriver;
import com.gelerion.open.storage.s3.model.S3StoragePath;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import net.jodah.failsafe.function.CheckedRunnable;
import net.jodah.failsafe.function.CheckedSupplier;

public class Invoker {
    private final S3Storage storage;
    private final InvokerConfig config;

    public Invoker(S3Storage storage, InvokerConfig config) {
        this.storage = storage;
        this.config = config;
    }

    public <R> R retryIfBucketNotExist(StoragePath<?> path, FunctionExceptional<S3StoragePath<?>, R> logic) {
        return exec(() -> {
            S3StoragePath<?> s3Path = (S3StoragePath<?>) path;

            RetryPolicy<R> onMissingBucketPolicy = RetryOnMissingBucketPolicy
                    .withCreateBucketFn(attempt -> storage.createBucket(s3Path.bucket()));

            return Failsafe.with(onMissingBucketPolicy).get(() -> logic.execute(s3Path));
        });
    }

    public <R> RetryDriver<R> retry() {
        /*
                 .maxAttempts(maxAttempts)
                    .exponentialBackoff(BACKOFF_MIN_SLEEP, maxBackoffTime, maxRetryTime, 2.0)
                    .stopOn(InterruptedException.class, UnrecoverableS3OperationException.class)
                    .onRetry(STATS::newGetMetadataRetry)
                    .run("getS3ObjectMetadata", () -> {
                        try {
                            STATS.newMetadataCall();
                            return s3.getObjectMetadata(bucketName, key);
                        }
         */

        return RetryDriver.withPolicy(config.createDefaultPolicy());
    }

    public <T> T exec(CheckedSupplier<T> supplier) {
        return RetryDriver.withPolicy(config.createDefaultPolicy())
                .exec(supplier);
    }

    public void run(CheckedRunnable runnable) {
        RetryDriver.withPolicy(config.createDefaultPolicy())
                .run(runnable);
    }
}

