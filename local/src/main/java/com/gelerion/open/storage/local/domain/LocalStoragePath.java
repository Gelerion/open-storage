package com.gelerion.open.storage.local.domain;

import com.gelerion.open.storage.api.domain.StoragePath;
import com.gelerion.open.storage.api.dsl.PathImplCheckerDsl;
import com.gelerion.open.storage.api.exceptions.StorageOperationException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;


public abstract class LocalStoragePath<T extends StoragePath<T>> implements StoragePath<T> {
    static final Path ROOT = Paths.get("").toAbsolutePath().getRoot();
    private final PathImplCheckerDsl<LocalStorageFile, LocalStorageDirectory> dsl;
    final Path workingPath;


    protected LocalStoragePath(Path path) {
        Objects.requireNonNull(path, "Path must be provided");
        this.workingPath = path;
        this.dsl = PathImplCheckerDsl.create(LocalStorageFile.class, LocalStorageDirectory.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R unwrap(Class<R> clazz) {
        if (clazz.isAssignableFrom(workingPath.getClass())) {
            return (R) workingPath;
        }
        throw new StorageOperationException("Unwrapping wrong instance");
    }

    @Override
    public LocalStorageDirectory parentDir() {
        return workingPath.getParent() != null ?
                LocalStorageDirectory.get(workingPath.getParent()) : absolutePathParent();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X extends StoragePath<?>> X resolve(X that) {
        return dsl.checkValidImplOrFail(that)
                .whenFile(file -> (X) resolve(file))
                .whenDir(dir   -> (X) resolve(dir));
    }

    public abstract LocalStorageFile resolve(LocalStorageFile file);

    public abstract LocalStorageDirectory resolve(LocalStorageDirectory dir);

    public String asString() {
        return workingPath.toString();
    }

    @Override
    public int compareTo(StoragePath that) {
        return workingPath.compareTo(Paths.get(that.toString()));
    }

    @Override
    public String toString() {
        return asString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalStoragePath<?> that = (LocalStoragePath<?>) o;
        return Objects.equals(workingPath, that.workingPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workingPath);
    }

    private LocalStorageDirectory absolutePathParent() {
        if (isRoot()) {
            return LocalStorageDirectory.get(ROOT);
        }

        Path parent = workingPath.toAbsolutePath().getParent();
        return LocalStorageDirectory.get(parent);
    }

    private boolean isRoot() {
        return workingPath.toAbsolutePath().equals(ROOT);
    }
}
