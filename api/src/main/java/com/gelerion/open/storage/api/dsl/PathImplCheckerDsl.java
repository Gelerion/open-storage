package com.gelerion.open.storage.api.dsl;

import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.domain.StoragePath;
import com.gelerion.open.storage.api.exceptions.StorageOperationException;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.gelerion.open.storage.api.ops.StorageOperations.requireNonNull;

public class PathImplCheckerDsl<FileImpl extends StorageFile, DirImpl extends StorageDirectory> {
    private final Predicate<StoragePath<?>> isFile;
    private final Predicate<StoragePath<?>> isDir;

    private PathImplCheckerDsl(Predicate<StoragePath<?>> isFile, Predicate<StoragePath<?>> isDir) {
        this.isFile = isFile;
        this.isDir = isDir;
    }

    public static <F extends StorageFile, D extends StorageDirectory> PathImplCheckerDsl<F, D> create(Class<F> fileImpl, Class<D> dirImpl) {
        return new PathImplCheckerDsl<>(fileImpl::isInstance, dirImpl::isInstance);
    }

    public OnFileAction checkValidImplOrFail(StoragePath<?> path) {
        if (isFile.or(isDir).negate().test(requireNonNull(path))) {
            throw new StorageOperationException("path must have valid implementation, actual implementation is --" + path.getClass().getSimpleName());
        }
        return new OnFileAction(path);
    }

    public class OnFileAction {
        private final StoragePath<?> path;

        private OnFileAction(StoragePath<?> path) {
            this.path = path;
        }

        @SuppressWarnings("unchecked")
        public IfDirAction ifFile(Consumer<FileImpl> onFileFunc) {
            if (isFile.test(path))
                onFileFunc.accept((FileImpl) path);
            return new IfDirAction(path);
        }

        @SuppressWarnings("unchecked")
        public <R> OnDirAction<R> whenFile(Function<FileImpl, R> onFileFunc) {
            if (isFile.test(path)) {
                R result = onFileFunc.apply((FileImpl) path);
                return new NoOpOnDirAction<>(result);
            }
            else
                return new OnDirActionImpl<>(path);

        }
    }

    public abstract class OnDirAction<Result> {
        public abstract Result whenDir(Function<DirImpl, Result> onDirFunc);
    }

    public class OnDirActionImpl<T> extends OnDirAction<T> {
        private final StoragePath<?> path;

        private OnDirActionImpl(StoragePath<?> path) {
            this.path = path;
        }

        @SuppressWarnings("unchecked")
        public T whenDir(Function<DirImpl, T> onDirFunc) {
            return onDirFunc.apply((DirImpl) path);
        }

    }

    public class NoOpOnDirAction<T> extends OnDirAction<T> {
        private final T result;

        NoOpOnDirAction(T result) {
            this.result = result;
        }

        public T whenDir(Function<DirImpl, T> onDirFunc) {
            return result;
        }
    }

    public class IfDirAction {
        private final StoragePath<?> path;

        private IfDirAction(StoragePath<?> path) {
            this.path = path;
        }

        @SuppressWarnings("unchecked")
        public void ifDir(Consumer<DirImpl> onDirFunc) {
            if (isDir.test(path))
                onDirFunc.accept((DirImpl) path);
        }
    }
}
