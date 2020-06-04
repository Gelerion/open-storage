package com.gelerion.open.storage.api.copy.flow;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.copy.CopyTask;
import com.gelerion.open.storage.api.copy.DifferentStoragesCopyTask;
import com.gelerion.open.storage.api.copy.SameStorageCopyTask;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.domain.StoragePath;

import java.util.Objects;
import java.util.stream.Stream;

import static com.gelerion.open.storage.api.copy.flow.SourceSpec.path;
import static com.gelerion.open.storage.api.copy.flow.TargetSpec.dir;

public class CopyFlow implements CopySource, CopyTarget {
    private Storage storage;
    private CopyFlow.Source source;
    private CopyFlow.Target target;

    public CopyFlow() {
    }

    public CopyFlow(Storage storage) {
        this.storage = storage;
    }

    public CopyTarget source(StoragePath path) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(storage);
        return source(storage, path);
    }

    public CopyTarget source(Storage storage, StoragePath path) {
        Objects.requireNonNull(path);
        return source(storage, path(path));
    }

    public CopyTarget source(SourceSpec spec) {
        Objects.requireNonNull(spec);
        Objects.requireNonNull(storage);
        return source(storage, spec);
    }

    public CopyTarget source(Storage storage, SourceSpec spec) {
        Objects.requireNonNull(storage);
        Objects.requireNonNull(spec);
        this.source = new CopyFlow.Source(spec.withStorage(storage));
        return this;
    }

    @Override
    public CopyTask target(StorageDirectory dir) {
        Objects.requireNonNull(dir);
        Objects.requireNonNull(storage);
        return target(storage, dir);
    }

    @Override
    public CopyTask target(Storage storage, StorageDirectory dir) {
        Objects.requireNonNull(dir);
        Objects.requireNonNull(storage);
        return target(storage, dir(dir));
    }

    @Override
    public CopyTask target(TargetSpec spec) {
        Objects.requireNonNull(spec);
        return target(storage, spec);
    }

    @Override
    public CopyTask target(Storage storage, TargetSpec spec) {
        Objects.requireNonNull(spec);
        Objects.requireNonNull(storage);
        this.target = new CopyFlow.Target(spec.withStorage(storage));

        if (source.storage().name().equals(target.storage().name())) {
            return new SameStorageCopyTask(source, target);
        }

        return new DifferentStoragesCopyTask(source, target);
    }

    public static class Source {
        private final SourceSpec sourceSpec;

        private Source(SourceSpec sourceSpec) {
            this.sourceSpec = sourceSpec;
        }

        public Stream<StorageFile> files() {
            return sourceSpec.files();
        }

        public Storage storage() {
            return sourceSpec.sourceStorage;
        }
    }

    public static class Target {
        private final TargetSpec targetSpec;

        public Target(TargetSpec targetSpec) {
            this.targetSpec = targetSpec;
        }

//        public StorageFile resolve(StorageFile sourceFile) {
//            return targetSpec.resolve(sourceFile);
//        }

        public StorageDirectory dir() {
            return targetSpec.dir();
        }

        //todo handle absolute path - say s3a:// to file://
        public StorageFile resolveTargetPath(StorageFile sourceFile) {
            final StorageFile file = dir().toStorageFile(sourceFile.fileName());
            return targetSpec.applyTransformations(file);
        }

        public Storage storage() {
            return targetSpec.targetStorage;
        }
    }
}
