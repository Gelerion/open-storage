package com.gelerion.open.storage.api.rename;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.domain.StoragePath;

import java.util.Objects;
import java.util.function.Function;

public abstract class StoragePathRenamer<T extends StoragePath<T>> implements Renamer<T> {
    private final T source;
    private final Storage storage;

    protected StoragePathRenamer(T source, Storage storage) {
        this.source = source;
        this.storage = storage;
    }

    @Override
    public T to(String target) {
        Objects.requireNonNull(target);
        return storage.move(source, source.rename(target));
    }

    @Override
    public T to(T target) {
        return storage.move(source, source.rename(target.name()));
    }

    @Override
    public T to(Function<T, T> func) {
        return storage.move(source, func.apply(source));
    }
}
