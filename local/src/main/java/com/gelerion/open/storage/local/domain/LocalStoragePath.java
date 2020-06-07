package com.gelerion.open.storage.local.domain;

import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.domain.StoragePath;
import com.gelerion.open.storage.api.exceptions.StorageOperationException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public abstract class LocalStoragePath implements StoragePath {
    static final Path ROOT = Paths.get("").toAbsolutePath().getRoot();
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

    @Override
    public LocalStorageDirectory parentDir() {
        return currentPath.getParent() != null ?
                LocalStorageDirectory.get(currentPath.getParent()) : absolutePathParent();
    }

    @Override
    public StoragePath resolve(StoragePath other) {
        if (other instanceof LocalStorageFile)
            return resolve((LocalStorageFile) other);
        else if(other instanceof LocalStorageDirectory)
            return resolve((LocalStorageDirectory) other);
        throw new StorageOperationException("StoragePath must be either LocalStorageFile or LocalStorageFolder");
    }


    @Override
    public abstract LocalStorageFile resolve(StorageFile file);

    @Override
    public abstract LocalStorageDirectory resolve(StorageDirectory dir);

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

    private LocalStorageDirectory absolutePathParent() {
        if (isRoot()) {
            return LocalStorageDirectory.get(ROOT);
        }

        Path parent = currentPath.toAbsolutePath().getParent();
        return LocalStorageDirectory.get(parent);
    }

    private boolean isRoot() {
        return currentPath.toAbsolutePath().equals(ROOT);
    }
}
