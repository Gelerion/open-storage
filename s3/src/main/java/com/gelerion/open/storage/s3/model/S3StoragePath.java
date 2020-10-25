package com.gelerion.open.storage.s3.model;

import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StoragePath;
import com.gelerion.open.storage.s3.utils.S3PathSplitter;
import com.gelerion.open.storage.s3.utils.S3PathSplitter.BucketAndKey;

public abstract class S3StoragePath<T extends StoragePath<T>> implements StoragePath<T> {
    protected final String workingPath;
    protected final String bucket;
    protected final String key;

    protected S3StoragePath(String path) {
        BucketAndKey bucketAndKey = S3PathSplitter.split(path);
        this.workingPath = path;
        this.bucket = bucketAndKey.bucket();
        this.key = bucketAndKey.key();
    }

    public String bucket() {
        return bucket;
    }

    public String key() {
        return key;
    }

    @Override
    public <X> X unwrap(Class<X> clazz) {
        return null;
    }

    @Override
    public StorageDirectory parentDir() {
        return null;
    }

    @Override
    public <X extends StoragePath<?>> X resolve(X that) {
        return null;
    }

    @Override
    public T rename(String target) {
        return null;
    }

    @Override
    public T butLast() {
        return null;
    }

    @Override
    public int compareTo(StoragePath that) {
        return workingPath.compareTo(that.toString());
    }

    @Override
    public String toString() {
        return asString();
    }

    public String asString() {
        return workingPath;
    }

}
