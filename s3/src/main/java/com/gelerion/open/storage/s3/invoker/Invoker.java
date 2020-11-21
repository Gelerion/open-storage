package com.gelerion.open.storage.s3.invoker;

import com.gelerion.open.storage.s3.exceptions.recoverable.S3StorageRecoverableException;
import com.gelerion.open.storage.s3.invoker.retry.RetryDriver;
import net.jodah.failsafe.function.CheckedRunnable;
import net.jodah.failsafe.function.CheckedSupplier;

public class Invoker {
    private final InvokerConfig config;

    public Invoker(InvokerConfig config) {
        this.config = config;
    }

    public static Invoker getDefault() {
        return new Invoker(InvokerConfig.getDefault());
    }

    public <R> RetryDriver<R> customize() {
        return RetryDriver.withPolicy(config.createDefaultPolicy());
    }

    public <T> T exec(CheckedSupplier<T> supplier) {
        return RetryDriver.withPolicy(config.createDefaultPolicy())
                .retryOn(S3StorageRecoverableException.class)
                .exec(supplier);
    }

    public void run(CheckedRunnable runnable) {
        RetryDriver.withPolicy(config.createDefaultPolicy())
                .retryOn(S3StorageRecoverableException.class)
                .run(runnable);
    }
}

