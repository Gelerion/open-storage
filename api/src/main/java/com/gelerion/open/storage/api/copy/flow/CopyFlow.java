package com.gelerion.open.storage.api.copy.flow;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.copy.CopyTask;
import com.gelerion.open.storage.api.copy.ForeignStorageCopyTask;
import com.gelerion.open.storage.api.copy.factory.CopyTaskFactory;
import com.gelerion.open.storage.api.copy.resolver.TargetPathResolver;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.domain.StoragePath;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.gelerion.open.storage.api.copy.flow.SourceSpec.path;

public class CopyFlow implements CopySource, CopyTarget {
    private Storage storage;
    private CopyFlow.Source source;
    private CopyFlow.Target target;

    public CopyFlow() {
    }

    public CopyFlow(Storage storage) {
        this.storage = storage;
    }

    public CopyTarget source(StoragePath<?> path) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(storage);
        return source(storage, path);
    }

    public CopyTarget source(Storage storage, StoragePath<?> path) {
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
        return target(storage, TargetSpec.path(dir));
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

        if (source.storage().scheme().equals(target.storage().scheme())) {
            return CopyTaskFactory.getProvider(source.storage().scheme()).createCopyTask(source, target);
        }

        return new ForeignStorageCopyTask(source, target);
    }

    public static class Source {
        private final SourceSpec spec;

        private Source(SourceSpec spec) {
            this.spec = spec;
        }

        public Stream<StorageFile> files() {
            return spec.files();
        }

        public Storage storage() {
            return spec.sorage;
        }
    }

    public static class Target {
        private final TargetSpec spec;

        public Target(TargetSpec spec) {
            this.spec = spec;
        }

        public StorageDirectory dir() {
            return spec.path();
        }

        public StorageFile applyTransformations(StorageFile file) {
            return spec.applyTransformations(file);
        }

        public TargetPathResolver resolver(List<StorageFile> sourceFiles, boolean flatten) {
            return TargetPathResolver.get(this, sourceFiles, flatten);
        }

        public Storage storage() {
            return spec.targetStorage;
        }

    }
}
