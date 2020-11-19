package com.gelerion.open.storage.s3.invoker.retry;

import com.gelerion.open.storage.s3.invoker.translator.AwsExceptionsTranslator;
import com.gelerion.open.storage.s3.invoker.translator.DefaultAwsExceptionsTranslator;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import net.jodah.failsafe.event.ExecutionCompletedEvent;
import net.jodah.failsafe.function.CheckedConsumer;
import net.jodah.failsafe.function.CheckedRunnable;
import net.jodah.failsafe.function.CheckedSupplier;

public class RetryDriver<R> extends PolicyConfigurer<R, RetryDriver<R>> {
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
        return Failsafe.with(policy).get(translateException(supplier));
    }

    public void run(CheckedRunnable runnable) {
        Failsafe.with(policy).run(translateException(runnable));
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
}

/*
The S3A request retry policy. This uses the retry options in the configuration file to determine retry
count and delays for "normal" retries and separately, for throttling; the latter is best handled for longer with an exponential back-off.
Those exceptions considered unrecoverable (networking) are failed fast.
All non-IOEs are failed immediately. Assumed: bugs in code, unrecoverable errors, etc
 */

/*
  @FunctionalInterface
  public interface Retried {
        void onFailure(String text, IOException exception, int retries, boolean idempotent);
  }
  private final Retried onRetry = this::operationRetried;

  RetryPolicy {
        RetryAction shouldRetry(Exception e, int retries, int failovers, boolean isIdempotentOrAtMostOnce) throws Exception;
  }

 invoker = new Invoker(new S3ARetryPolicy(getConf()), onRetry);

  S3ARetryPolicy {
    int limit = conf.getInt(RETRY_LIMIT, RETRY_LIMIT_DEFAULT); //7
    long interval = conf.getTimeDuration(RETRY_INTERVAL, RETRY_INTERVAL_DEFAULT, TimeUnit.MILLISECONDS) //500ms
    retryIdempotentCalls = new FailNonIOEs(new IdempotencyRetryFilter(baseExponentialRetry));
    throttlePolicy = createThrottleRetryPolicy(conf); // exponentialBackoffRetry --> RETRY_THROTTLE_LIMIT, RETRY_THROTTLE_INTERVAL
    connectivityFailure = baseExponentialRetry;
    Map<Class<? extends Exception>, RetryPolicy> policyMap = createExceptionMap();
    retryPolicy = retryByException(retryIdempotentCalls, policyMap);
  }

  PolicyMap:
  // throttled requests are can be retried, always
  policyMap.put(AWSServiceThrottledException.class, throttlePolicy);

  // connectivity problems are retried without worrying about idempotency
  policyMap.put(ConnectTimeoutException.class, connectivityFailure);

  // Status 500 error code is also treated as a connectivity problem
  policyMap.put(AWSStatus500Exception.class, connectivityFailure);

  // server didn't respond.
  policyMap.put(AWSNoResponseException.class, retryIdempotentCalls);

  // other operations
  policyMap.put(AWSClientIOException.class, retryIdempotentCalls);
  policyMap.put(AWSServiceIOException.class, retryIdempotentCalls);
  policyMap.put(AWSS3IOException.class, retryIdempotentCalls);
  policyMap.put(SocketTimeoutException.class, retryIdempotentCalls);


  FailNonIOEs {
   e instanceof IOException ? next.shouldRetry(e, retries, failovers, true) : RetryAction.FAIL;
  }

  IdempotencyRetryFilter {
    public RetryAction shouldRetry(Exception e, int retries, int failovers, boolean idempotent) throws Exception {
      return idempotent ? next.shouldRetry(e, retries, failovers, true) : RetryAction.FAIL;
  }

 */

//TODO: handle throtle exceptions
// Recoverable--Unrecoverable exceptions
//-----------------------------------
// == Unrecoverable
// 400 - HTTP_BAD_REQUEST
// 401-403 access denied
//403 - HTTP_FORBIDDEN
//404 - HTTP_NOT_FOUND
// + InterruptedException
//-----------------------------------

/*
    case 404:
        if (isUnknownBucket(ase)) {
          // this is a missing bucket
          ioe = new UnknownStoreException(path, ase);
        } else {
          // a normal unknown object
          ioe = new FileNotFoundException(message);
          ioe.initCause(ase);
        }


      // this also surfaces sometimes and is considered to
      // be ~ a not found exception.
      case 410:
        ioe = new FileNotFoundException(message);
        ioe.initCause(ase);
        break;

      // method not allowed; seen on S3 Select.
      // treated as a bad request
      case 405:
        ioe = new AWSBadRequestException(message, s3Exception);
        break;

      // out of range. This may happen if an object is overwritten with
      // a shorter one while it is being read.
      case 416: (HTTP_RANGE_NOT_SATISFIABLE)
        ioe = new EOFException(message);
        ioe.initCause(ase);
        break;

      // throttling
      case 503:
        ioe = new AWSServiceThrottledException(message, ase);
        break;

      // internal error
      case 500:
        ioe = new AWSStatus500Exception(message, ase);
        break;

      case 200:
        if (exception instanceof MultiObjectDeleteException) {
          // failure during a bulk delete
          return translateDeleteException(message,
              (MultiObjectDeleteException) exception);
        }
        // other 200: FALL THROUGH
 */