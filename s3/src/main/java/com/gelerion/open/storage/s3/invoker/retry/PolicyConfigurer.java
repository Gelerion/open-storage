package com.gelerion.open.storage.s3.invoker.retry;

import net.jodah.failsafe.event.ExecutionCompletedEvent;
import net.jodah.failsafe.function.CheckedConsumer;

public abstract class PolicyConfigurer<R, S extends PolicyConfigurer<R, S>> {

    public abstract S onFailure(CheckedConsumer<? extends ExecutionCompletedEvent<R>> listener);

    public abstract S onSuccess(CheckedConsumer<? extends ExecutionCompletedEvent<R>> listener);
}
