package com.gelerion.open.storage.s3.domain;

import com.gelerion.open.storage.api.domain.StorageFile;

public class S3StorageFile extends S3StoragePath<StorageFile> implements StorageFile {

    protected S3StorageFile(String path) {
        super(path);
    }

    public static StorageFile get(String path) {
        return new S3StorageFile(path);
    }

//    public static S3StorageFile get(String key) {
//
//    }


    @Override
    public String fileName() {
        return !key.contains("/") ? key : key.substring(key.lastIndexOf("/") + 1);
    }
}
