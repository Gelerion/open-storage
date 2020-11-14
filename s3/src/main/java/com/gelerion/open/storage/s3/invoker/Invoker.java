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

    public Invoker retry() {
        return this;
    }

    public void test() {
        //new RetryPolicy<String>()
             //   .handleIf() //recoverable error

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