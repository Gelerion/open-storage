package com.gelerion.open.storage.s3.invoker;

import com.gelerion.open.storage.s3.exceptions.UnrecoverableS3Exception;
import net.jodah.failsafe.RetryPolicy;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.SECONDS;

public class InvokerConfig {
    private final int retryLimit;
    private final Duration retryDelay;
    private final Duration maxBackoffTime;
    private final double backOffDelayFactor;
    private final double jitterFactor;

    public InvokerConfig() {
        retryLimit = 5;
        retryDelay = Duration.of(100, MILLIS);
        maxBackoffTime = Duration.of(1, SECONDS);
        backOffDelayFactor = 1.3;
        jitterFactor = 0.15;
    }

    public static InvokerConfig getDefault() {
        return new InvokerConfig();
    }

    public <R> RetryPolicy<R> createDefaultPolicy() {
        return new RetryPolicy<R>()
                .withMaxAttempts(retryLimit)
                .withJitter(jitterFactor)
                .withBackoff(retryDelay.toMillis(), maxBackoffTime.toMillis(), MILLIS, backOffDelayFactor)
                .abortOn(InterruptedException.class, UnrecoverableS3Exception.class);
    }
}
