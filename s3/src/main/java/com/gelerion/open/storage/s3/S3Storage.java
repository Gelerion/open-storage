package com.gelerion.open.storage.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.copy.flow.CopySource;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.domain.StoragePath;
import com.gelerion.open.storage.api.ops.ListFilesOption;
import com.gelerion.open.storage.api.reader.StorageReader;
import com.gelerion.open.storage.api.rename.Renamer;
import com.gelerion.open.storage.api.writer.StorageWriter;
import com.gelerion.open.storage.s3.provider.AwsClientsProvider;

import java.util.Set;

public class S3Storage implements Storage {
    private final AmazonS3 s3;

    public static S3Storage newS3Storage() {
        return newS3Storage(AwsClientsProvider.getDefault());
    }

    public static S3Storage newS3Storage(AwsClientsProvider clientsProvider) {
        return new S3Storage(clientsProvider.s3Client());
    }

    public S3Storage(AmazonS3 s3) {
        this.s3 = s3;
    }

    @Override
    public String scheme() {
        return null;
    }

    @Override
    public <T extends StoragePath<T>> Storage create(T path) {
        return null;
    }

    public Storage create(StorageDirectory directory) {
        return null;
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

    @Override
    public StorageWriter writer(StorageFile file) {
        return null;
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
        return null;
    }

    @Override
    public <T extends StoragePath<T>> T absolutePath(T path) {
        return null;
    }
}
