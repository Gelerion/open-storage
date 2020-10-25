package com.gelerion.open.storage.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.copy.flow.CopySource;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.domain.StoragePath;
import com.gelerion.open.storage.api.ops.ListFilesOption;
import com.gelerion.open.storage.api.reader.StorageReader;
import com.gelerion.open.storage.api.rename.Renamer;
import com.gelerion.open.storage.api.writer.StorageWriter;
import com.gelerion.open.storage.s3.model.S3StorageDirectory;
import com.gelerion.open.storage.s3.model.S3StorageFile;
import com.gelerion.open.storage.s3.invoker.Invoker;
import com.gelerion.open.storage.s3.provider.AwsClientsProvider;
import com.gelerion.open.storage.s3.writer.S3StorageWriter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;

//TODO: retry logic on retryable errors
public class S3Storage implements Storage {
    private final AmazonS3 s3;
    private final Invoker invoker;

    public static S3Storage newS3Storage() {
        return newS3Storage(AwsClientsProvider.getDefault());
    }

    public static S3Storage newS3Storage(AwsClientsProvider clientsProvider) {
        return new S3Storage(clientsProvider.s3Client());
    }

    public S3Storage(AmazonS3 s3) {
        this.s3 = s3;
        this.invoker = new Invoker(this);
    }

    @Override
    public String scheme() {
        return null;
    }

    @Override
    public <T extends StoragePath<T>> Storage create(T path) {
        return invoker.retryIfBucketNotExist(path, s3Path -> {
            // create meta-data for your folder and set content-length to 0
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(0);

            // create empty content
            InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

            s3.putObject(s3Path.bucket(), s3Path.key(), emptyContent, metadata);
            return this;
        });
    }

    @Override
    public <T extends StoragePath<T>> void delete(T path) {

    }

    @Override
    public <T extends StoragePath<T>> long size(T path) {
        return 0;
    }

    @Override
    public StorageReader reader(StorageFile file) {
        return null;
    }

    //TODO: refactor cast
    @Override
    public StorageWriter writer(StorageFile file) {
        return S3StorageWriter.output((S3StorageFile) file, s3);
    }

    @Override
    public boolean exists(StoragePath<?> path) {
        return false;
    }

    @Override
    public <T extends StoragePath<T>> Renamer<T> rename(T source) {
        return null;
    }

    @Override
    public <T extends StoragePath<T>> T move(T source, T target) {
        return null;
    }

    @Override
    public CopySource copy() {
        return null;
    }

    @Override
    public Set<StorageFile> files(StorageDirectory underDir, ListFilesOption... opts) {
        return null;
    }

    @Override
    public Set<StorageDirectory> dirs(StorageDirectory underDir) {
        return s3.listBuckets().stream().map(bucket -> S3StorageDirectory.get(bucket.getName())).collect(Collectors.toSet());
    }

    @Override
    public <T extends StoragePath<T>> T absolutePath(T path) {
        return null;
    }

    public void createBucket(String bucket) {
        s3.createBucket(bucket);
    }
}
