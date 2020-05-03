package com.gelerion.open.storage.api.copy.flow;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.copy.CopyTask;
import com.gelerion.open.storage.api.copy.CopyTaskImpl;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.domain.StoragePath;

import java.util.Objects;
import java.util.stream.Stream;

import static com.gelerion.open.storage.api.copy.flow.FromSpec.path;
import static com.gelerion.open.storage.api.copy.flow.ToSpec.dir;

public class CopyFlow implements CopyFrom, CopyTo {
    private Storage storage;
    private CopyFlow.Source source;
    private CopyFlow.Target target;

    public CopyFlow() {
    }

    public CopyFlow(Storage storage) {
        this.storage = storage;
    }

    public CopyTo from(StoragePath path) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(storage);
        return from(storage, path);
    }

    public CopyTo from(Storage storage, StoragePath path) {
        Objects.requireNonNull(path);
        return from(storage, path(path));
    }

    public CopyTo from(FromSpec spec) {
        Objects.requireNonNull(spec);
        Objects.requireNonNull(storage);
        return from(storage, spec);
    }

    public CopyTo from(Storage storage, FromSpec spec) {
        Objects.requireNonNull(storage);
        Objects.requireNonNull(spec);
        this.source = new CopyFlow.Source(spec.withStorage(storage));
        return this;
    }

    @Override
    public CopyTask to(StorageDirectory dir) {
        Objects.requireNonNull(dir);
        Objects.requireNonNull(storage);
        return to(storage, dir);
    }

    @Override
    public CopyTask to(Storage storage, StorageDirectory dir) {
        Objects.requireNonNull(dir);
        Objects.requireNonNull(storage);
        return to(storage, dir(dir));
    }

    @Override
    public CopyTask to(ToSpec spec) {
        Objects.requireNonNull(spec);
        return to(storage, spec);
    }

    @Override
    public CopyTask to(Storage storage, ToSpec spec) {
        Objects.requireNonNull(spec);
        Objects.requireNonNull(storage);
        this.target = new CopyFlow.Target(spec.withStorage(storage));
        return new CopyTaskImpl(source, target);
    }

    public static class Source {
        private final FromSpec fromSpec;

        private Source(FromSpec fromSpec) {
            this.fromSpec = fromSpec;
        }

        public Stream<StorageFile> files() {
            return fromSpec.files();
        }

        public Storage storage() {
            return fromSpec.sourceStorage;
        }
    }

    public static class Target {
        private final ToSpec toSpec;

        public Target(ToSpec toSpec) {
            this.toSpec = toSpec;
        }

        public StorageFile resolve(StorageFile sourceFile) {
            return toSpec.resolve(sourceFile);
        }

        public StorageDirectory dir() {
            return toSpec.dir();
        }

        public Storage storage() {
            return toSpec.targetStorage;
        }
    }
}
