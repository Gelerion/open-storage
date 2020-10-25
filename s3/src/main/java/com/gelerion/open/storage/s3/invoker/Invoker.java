package com.gelerion.open.storage.s3.invoker;

import com.gelerion.open.storage.api.domain.StoragePath;
import com.gelerion.open.storage.api.ops.StorageOperations.FunctionExceptional;
import com.gelerion.open.storage.s3.S3Storage;
import com.gelerion.open.storage.s3.model.S3StoragePath;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;

import static com.gelerion.open.storage.api.ops.StorageOperations.exec;

public class Invoker {
    private final S3Storage storage;

    public Invoker(S3Storage storage) {
        this.storage = storage;
    }

    public <R> R retryIfBucketNotExist(StoragePath<?> path, FunctionExceptional<S3StoragePath<?>, R> logic) {
        return exec(() -> {
            S3StoragePath<?> s3Path = (S3StoragePath<?>) path;

            RetryPolicy<R> onMissingBucketPolicy = RetryOnMissingBucketPolicy
                    .withCreateBucketFn(attempt -> storage.createBucket(s3Path.bucket()));

            return Failsafe.with(onMissingBucketPolicy).get(() -> logic.execute(s3Path));
        });
    }
}
