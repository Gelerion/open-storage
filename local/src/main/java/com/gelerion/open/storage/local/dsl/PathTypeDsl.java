package com.gelerion.open.storage.local.dsl;

import com.gelerion.open.storage.api.domain.StoragePath;
import com.gelerion.open.storage.api.exceptions.StorageOperationException;
import com.gelerion.open.storage.local.domain.LocalStorageDirectory;
import com.gelerion.open.storage.local.domain.LocalStorageFile;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class PathTypeDsl {
    private static final Predicate<StoragePath<?>> isLocalDir = it -> it instanceof LocalStorageDirectory;
    private static final Predicate<StoragePath<?>> isLocalFile = it -> it instanceof LocalStorageFile;

    private final StoragePath<?> path;

    private PathTypeDsl(StoragePath<?> path) {
        checkLocal(path);
        this.path = path;
    }

    public static PathTypeDsl checkLocalOrFail(StoragePath<?> path) {
        return new PathTypeDsl(path);
    }

    public <R> ResultOrNothing<R> whenFile(Function<LocalStorageFile, R> func) {
        if (isLocalFile.test(path))
            return ResultOrNothing.of(func.apply((LocalStorageFile) path), path);
        else
            return ResultOrNothing.nothing(path);

    }

    public PathTypeDsl ifFile(Consumer<LocalStorageFile> func) {
        if (isLocalFile.test(path))
            func.accept((LocalStorageFile) path);

        return this;
    }

    public void ifDir(Consumer<LocalStorageDirectory> func) {
        if (isLocalDir.test(path))
            func.accept((LocalStorageDirectory) path);
    }

    public static class ResultOrNothing<T> {

        private final T resultOrNull;
        private final StoragePath<?> path;

        private ResultOrNothing(T resultOrNull, StoragePath<?> path) {
            this.resultOrNull = resultOrNull;
            this.path = path;
        }

        static <X> ResultOrNothing<X> nothing(StoragePath<?> path) {
            return new ResultOrNothing<>(null, path);
        }

        static <X> ResultOrNothing<X> of(X value, StoragePath<?> path) {
            if (value == null) {
                return new ResultOrNothing<>(null, path);
            }
            return new ResultOrNothing<>(value, path);
        }

        public T whenDir(Function<LocalStorageDirectory, T> func) {
            if (resultOrNull != null)  return resultOrNull;
            else return func.apply((LocalStorageDirectory) path);
        }
    }

    private void checkLocal(StoragePath<?> path) {
        if (isLocalDir.or(isLocalFile).negate().test(path)) {
            throw new StorageOperationException("path must be either LocalStorageDirectory or LocalStorageFile");
        }
    }
}
