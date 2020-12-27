package com.gelerion.open.storage.s3.model;

import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StoragePath;
import com.gelerion.open.storage.api.dsl.PathImplCheckerDsl;
import com.gelerion.open.storage.api.exceptions.StorageOperationException;
import com.gelerion.open.storage.s3.utils.S3KeySplitter;
import com.gelerion.open.storage.s3.utils.S3PathSplitter;
import com.gelerion.open.storage.s3.utils.S3PathSplitter.BucketAndKey;

//TODO: path auditing/lineage
//we want to track the path lineage from the creation
// e.g. when it was modified and how exactly, path.parentDir -> in a new path it should parent add an event and
//track the history created via constructor --> parentDir --> ....
public abstract class S3StoragePath<T extends StoragePath<T>> implements StoragePath<T> {
    private static final PathImplCheckerDsl<S3StorageFile, S3StorageDirectory> DSL = PathImplCheckerDsl
            .create(S3StorageFile.class, S3StorageDirectory.class);

    protected final String workingPath;
    protected final String bucket;
    protected final String key;

    protected S3StoragePath(String path) {
        BucketAndKey bucketAndKey = S3PathSplitter.split(path);
        this.workingPath = path;
        this.bucket = bucketAndKey.bucket();
        this.key = bucketAndKey.key();
    }

    public String bucket() {
        return bucket;
    }

    public String key() {
        return key;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X unwrap(Class<X> clazz) {
        if (clazz.isAssignableFrom(workingPath.getClass())) {
            return (X) workingPath;
        }
        throw new StorageOperationException("Unwrapping wrong instance");
    }

    @Override
    public StorageDirectory parentDir() {
        //TODO; extract schema from workingPath
        //TODO S3Key joiner
        return S3StorageDirectory.get("s3a://" + bucket + "/" + S3KeySplitter.split(key).butFirst().key());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X extends StoragePath<?>> X resolve(X that) {
        return DSL.checkValidImplOrFail(that)
                .whenFile(file -> (X) resolve(file))
                .whenDir(dir   -> (X) resolve(dir));
    }

    public abstract S3StorageFile resolve(S3StorageFile file);

    public abstract S3StorageDirectory resolve(S3StorageDirectory dir);

    @Override
    public boolean contains(String part) {
        return workingPath.contains(part);
    }

    @Override
    public int compareTo(StoragePath that) {
        return workingPath.compareTo(that.toString());
    }

    @Override
    public String toString() {
        return asString();
    }

    public String asString() {
        return workingPath;
    }
}
