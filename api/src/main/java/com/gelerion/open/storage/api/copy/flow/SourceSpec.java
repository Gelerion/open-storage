package com.gelerion.open.storage.api.copy.flow;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.domain.StoragePath;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class SourceSpec {
    protected OnSourceFileListener mapper;
    protected Predicate<StorageFile> predicate;
    protected Storage sourceStorage;

    public static SourceSpec file(StorageFile file) {
        Objects.requireNonNull(file);
        return new CopySourceFile(file);
    }

    public static SourceSpec dir(StorageDirectory dir) {
        Objects.requireNonNull(dir);
        return new CopySourceDir(dir);
    }

    //glob(...)

    public static SourceSpec path(StoragePath path) {
        return path instanceof StorageFile ? file((StorageFile) path) : dir((StorageDirectory) path);
    }


    SourceSpec withStorage(Storage storage) {
        this.sourceStorage = storage;
        return this;
    }

    public SourceSpec map(OnSourceFileListener mapper) {
        Objects.requireNonNull(mapper);
        if (this.mapper == null) this.mapper = mapper;
        else this.mapper = this.mapper.andThen(mapper);
        return this;
    }

    public SourceSpec filter(Predicate<StorageFile> predicate) {
        Objects.requireNonNull(predicate);
        if (this.predicate == null) this.predicate = predicate;
        else this.predicate = this.predicate.and(predicate);
        return this;
    }

    abstract Stream<StorageFile> files();
}
