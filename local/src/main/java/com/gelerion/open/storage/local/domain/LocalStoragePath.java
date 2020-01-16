package com.gelerion.open.storage.local.domain;

import com.gelerion.open.storage.api.domain.StoragePath;
import com.gelerion.open.storage.api.exceptions.StorageOperationException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public abstract class LocalStoragePath implements StoragePath {
    final Path currentPath;

    protected LocalStoragePath(Path path) {
        Objects.requireNonNull(path, "Path must be provided");
        this.currentPath = path;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R unwrap(Class<R> clazz) {
        if (clazz.isAssignableFrom(currentPath.getClass())) {
            return (R) currentPath;
        }
        throw new StorageOperationException("Unwrapping wrong instance");
    }


    public String asString() {
        return currentPath.toString();
    }

    @Override
    public int compareTo(StoragePath that) {
        return currentPath.compareTo(Paths.get(that.toString()));
    }

    @Override
    public String toString() {
        return asString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalStoragePath that = (LocalStoragePath) o;
        return Objects.equals(currentPath, that.currentPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentPath);
    }
}
