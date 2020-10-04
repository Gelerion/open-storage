package com.gelerion.open.storage.s3.domain;

import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;

public class S3StorageDirectory extends S3StoragePath<StorageDirectory> implements StorageDirectory {

    @Override
    public StorageFile toStorageFile(String fileName) {
        return null;
    }

    @Override
    public StorageDirectory addSubDirectory(String dir) {
        return null;
    }

    @Override
    public String dirName() {
        return null;
    }
}
