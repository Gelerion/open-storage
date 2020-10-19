package com.gelerion.open.storage.s3.utils;

import com.gelerion.open.storage.s3.exceptions.InvalidSchemaException;
import com.gelerion.open.storage.s3.exceptions.S3BucketMustBeProvidedException;

import java.util.Objects;

public class S3PathSplitter {

    public static BucketAndKey split(String path) {
        Objects.requireNonNull(path, "Path must not be empty");
        checkSchemeOrFail(path);
        try {
            String bucketAndKey = path.split("s3[n|a]?://")[1];
            String bucket = bucketAndKey.substring(0, bucketAndKey.indexOf("/"));
            String key    = bucketAndKey.substring(bucketAndKey.indexOf("/") + 1);
            return new BucketAndKey(bucket, key);
        } catch (Exception e) {
            throw new S3BucketMustBeProvidedException(path, e);
        }
    }

    private static void checkSchemeOrFail(String path) {
        if (!path.startsWith("s3")) {
            throw new InvalidSchemaException(path);
        }
    }

    public static class BucketAndKey {
        private final String bucket;
        private final String key;

        public BucketAndKey(String bucket, String key) {
            this.bucket = bucket;
            this.key = key;
        }

        public String bucket() {
            return bucket;
        }

        public String key() {
            return key;
        }

        @Override
        public String toString() {
            return "BucketAndKey{" +
                    "bucket='" + bucket + '\'' +
                    ", key='" + key + '\'' +
                    '}';
        }
    }

}
