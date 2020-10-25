package com.gelerion.open.storage.s3.model;

import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;

public class S3StorageDirectory extends S3StoragePath<StorageDirectory> implements StorageDirectory {

    protected S3StorageDirectory(String dir) {
        super(dir);
    }

//    public static S3StorageDirectory get(String dir, String... subDirs) {
//        return new S3StorageDirectory(dir);
//    }

    public static S3StorageDirectory get(String dir) {
        return new S3StorageDirectory(dir);
    }

    @Override
    public StorageFile toStorageFile(String fileName) {
        //TODO: check maybe
        return S3StorageFile.get(workingPath + "/" + fileName);
    }

    @Override
    public StorageDirectory addSubDirectory(String dir) {
        return null;
    }

    @Override
    public String dirName() {
        return !key.contains("/") ? key : key.substring(key.lastIndexOf("/") + 1);
    }
}
