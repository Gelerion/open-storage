package com.gelerion.open.storage.s3.domain;

import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StoragePath;

public class S3StoragePath<T extends StoragePath<T>> implements StoragePath<T> {
    private String workingPath;

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
    public String name() {
        return null;
    }

    @Override
    public T butLast() {
        return null;
    }

    @Override
    public int compareTo(StoragePath<T> o) {
        return 0;
    }

}
