package com.gelerion.open.storage.s3.invoker.translator;

@FunctionalInterface
public interface AwsExceptionsTranslator {

    Throwable translate(Throwable exception);

}
