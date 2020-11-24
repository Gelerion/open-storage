package com.gelerion.open.storage.s3.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.gelerion.open.storage.s3.invoker.Invoker;
import com.gelerion.open.storage.s3.model.S3StorageDirectory;

import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class S3ListObjectsStream {
    private final AmazonS3 s3;
    private final Invoker invoker;

    public S3ListObjectsStream(AmazonS3 s3) {
        this.s3 = s3;
        this.invoker = Invoker.getDefault();
    }

    public Stream<S3ObjectSummary> listObjects(S3StorageDirectory dir, boolean recursive) {
        Iterator<S3ObjectSummary> objectsIterator = new Iterator<S3ObjectSummary>() {
            private ListObjectsV2Request request = makeReq(dir, recursive);
            private ListObjectsV2Result lastResult;
            private Iterator<S3ObjectSummary> objects;

            {
                lastResult = invoker.exec(() -> s3.listObjectsV2(request));
                objects = lastResult.getObjectSummaries().iterator();
            }


            @Override
            public boolean hasNext() {
                fetchNextBatchIfNeeded();
                return objects.hasNext();
            }

            private void fetchNextBatchIfNeeded() {
                if (objects.hasNext()) {
                    return;
                }

                String token = lastResult.getNextContinuationToken();
                if (lastResult.isTruncated()) {
                    request = request.withContinuationToken(token);
                    lastResult = invoker.exec(() -> s3.listObjectsV2(request));
                    objects = lastResult.getObjectSummaries().iterator();
                    return;
                }

                objects = Collections.emptyIterator();
            }

            @Override
            public S3ObjectSummary next() {
                return objects.next();
            }
        };

        return asStream(objectsIterator);
    }

    private ListObjectsV2Request makeReq(S3StorageDirectory dir, boolean recursive) {
        ListObjectsV2Request request = new ListObjectsV2Request()
                .withBucketName(dir.bucket())
                .withPrefix(dir.key())
                .withMaxKeys(1000);

        if (!recursive) {
            request.setPrefix(dir.key()  + "/");
            request.setDelimiter("/");
        }

        return request;
    }

    private static <T> Stream<T> asStream(Iterator<T> sourceIterator) {
        Iterable<T> iterable = () -> sourceIterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
