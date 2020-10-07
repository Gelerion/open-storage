package com.gelerion.open.storage.s3.domain;

import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;

public class S3StorageDirectory extends S3StoragePath<StorageDirectory> implements StorageDirectory {

    protected S3StorageDirectory(String dir) {
        super(dir);
    }

//    public static S3StorageDirectory get(String dir, String... subDirs) {
//        return new LocalStorageDirectory(Paths.get(dir, subDirs));
//    }

    public static S3StorageDirectory get(String dir) {
        return new S3StorageDirectory(dir);
    }

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
