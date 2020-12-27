package com.gelerion.open.storage.api.copy.flow;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.copy.CopyTask;
import com.gelerion.open.storage.api.copy.ForeignStorageCopyTask;
import com.gelerion.open.storage.api.copy.factory.CopyTaskFactory;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.domain.StoragePath;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
        private final SourceSpec sourceSpec;

        private Source(SourceSpec sourceSpec) {
            this.sourceSpec = sourceSpec;
        }

        public Stream<StorageFile> files() {
            return sourceSpec.files();
        }

        public Storage storage() {
            return sourceSpec.sorage;
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
            return targetSpec.path();
        }

        //todo handle absolute path - say s3a:// to file://
        public Stream<StorageFile> resolveTargetPath(List<StorageFile> sourceFiles) {
            //file to file
            if (sourceFiles.size() == 1) {
                final StorageFile file = dir().toStorageFile(sourceFiles.get(0).fileName());
                return Stream.of(targetSpec.applyTransformations(file));
            }

            String[] paths = sourceFiles.stream().map(Object::toString).toArray(String[]::new);

            String commonPrefix = longestCommonPrefix(paths);

            //relativize
            List<StorageFile> res = sourceFiles.stream()
                    .map(Object::toString)
                    .map(path -> path.substring(commonPrefix.length()))
                    .map(relaitivazedPath -> dir().toStorageFile(relaitivazedPath))
                    .collect(Collectors.toList());

            return res.stream().map(targetSpec::applyTransformations);
        }

        public StorageFile resolveTargetPathFlatten(StorageFile sourceFile) {
            final StorageFile file = dir().toStorageFile(sourceFile.name());
            return targetSpec.applyTransformations(file);
        }

        public Storage storage() {
            return targetSpec.targetStorage;
        }

        String longestCommonPrefix(String[] strs) {
            if(strs == null || strs.length == 0) return "";
            String pre = strs[0];
            int i = 1;
            while(i < strs.length){
                while(strs[i].indexOf(pre) != 0)
                    pre = pre.substring(0,pre.length()-1);
                i++;
            }
            return pre;
        }
    }
}
