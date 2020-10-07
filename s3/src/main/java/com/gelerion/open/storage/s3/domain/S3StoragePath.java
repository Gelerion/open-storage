package com.gelerion.open.storage.s3.domain;

import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StoragePath;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class S3StoragePath<T extends StoragePath<T>> implements StoragePath<T> {
    private String workingPath;

    protected S3StoragePath(String path) {
        Objects.requireNonNull(path, "Path must be provided");
        this.workingPath = path;
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
    public String name() {
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
