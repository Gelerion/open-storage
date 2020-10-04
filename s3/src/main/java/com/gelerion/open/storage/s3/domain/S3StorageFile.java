package com.gelerion.open.storage.s3.domain;

import com.gelerion.open.storage.api.domain.StorageFile;

public class S3StorageFile extends S3StoragePath<StorageFile> implements StorageFile {

//    public static S3StorageFile get(String key) {
//
//    }


    @Override
    public String fileName() {
        return null;
    }
}
