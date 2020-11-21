package com.gelerion.open.storage.s3.invoker.retry;

import com.gelerion.open.storage.api.ops.StorageOperations;
import com.gelerion.open.storage.s3.invoker.translator.AwsExceptionsTranslator;
import com.gelerion.open.storage.s3.invoker.translator.DefaultAwsExceptionsTranslator;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import net.jodah.failsafe.event.ExecutionCompletedEvent;
import net.jodah.failsafe.function.CheckedConsumer;
import net.jodah.failsafe.function.CheckedRunnable;
import net.jodah.failsafe.function.CheckedSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.function.Predicate;

public class RetryDriver<R> extends PolicyConfigurer<R, RetryDriver<R>> {
    protected static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final RetryPolicy<R> policy;
    private final AwsExceptionsTranslator translator;

    private RetryDriver(RetryPolicy<R> policy) {
        this.policy = policy;
        this.translator = new DefaultAwsExceptionsTranslator();
    }

    public static <T> RetryDriver<T> withPolicy(RetryPolicy<T> policy) {
        return new RetryDriver<>(policy);
    }

    public <T extends R> T exec(CheckedSupplier<T> supplier) {
        return StorageOperations.exec(() ->
                Failsafe.with(policy).get(translateException(supplier))
        );
    }

    public void run(CheckedRunnable runnable) {
        StorageOperations.run(() ->
                Failsafe.with(policy).run(translateException(runnable))
        );
    }

    @Override
    public RetryDriver<R> onFailure(CheckedConsumer<? extends ExecutionCompletedEvent<R>> listener) {
        policy.onFailure(listener);
        return this;
    }

    @Override
    public RetryDriver<R> onSuccess(CheckedConsumer<? extends ExecutionCompletedEvent<R>> listener) {
        policy.onSuccess(listener);
        return this;
    }

    @SafeVarargs
    public final RetryDriver<R> retryOn(Class<? extends Throwable>... failures) {
        policy.handle(failures);
        return this;
    }

    public Then when(Predicate<? extends Throwable> failurePredicate) {
        policy.handleIf(failurePredicate);
        return new Then(failurePredicate);
    }

    private CheckedRunnable translateException(CheckedRunnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Throwable throwable) {
                throw translator.translate(throwable);
            }
        };
    }

    private <T> CheckedSupplier<T> translateException(CheckedSupplier<T> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (Throwable throwable) {
                throw translator.translate(throwable);
            }
        };
    }

    public class Then {
        private final Predicate<? extends Throwable> failurePredicate;

        public Then(Predicate<? extends Throwable> failurePredicate) {
            this.failurePredicate = failurePredicate;
        }

        @SuppressWarnings("unchecked")
        public RetryDriver<R> then(CheckedRunnable runnable) {
            policy.onFailedAttempt(attempt -> {
                if (((Predicate<Throwable>) failurePredicate).test(attempt.getLastFailure())) {
                    logFailure(runnable).run();
                }
            });

            return RetryDriver.this;
        }
    }

    private CheckedRunnable logFailure(CheckedRunnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Throwable throwable) {
                log.warn("Failed to execute 'then' statement, note this exceptions is ignored by the retry driver logic", throwable);
                throw throwable;
            }
        };
    }
}



