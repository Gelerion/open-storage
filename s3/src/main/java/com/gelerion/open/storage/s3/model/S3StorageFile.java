package com.gelerion.open.storage.s3.model;

import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.s3.utils.S3KeySplitter;

public class S3StorageFile extends S3StoragePath<StorageFile> implements StorageFile {

    protected S3StorageFile(String path) {
        super(path);
    }

    @Override
    public S3StorageFile resolve(S3StorageFile file) {
        return parentDir().resolve(file);
    }

    @Override
    public S3StorageDirectory resolve(S3StorageDirectory dir) {
        return parentDir().resolve(dir);
    }

    public static S3StorageFile get(String path) {
        return new S3StorageFile(path);
    }

//    public static S3StorageFile get(String key) {
//
//    }


    @Override
    public String fileName() {
        return !key.contains("/") ? key : key.substring(key.lastIndexOf("/") + 1);
    }

    @Override
    public StorageFile rename(String target) {
        //TODO: test
        return S3StorageFile.get("s3a://" + bucket + "/" + S3KeySplitter.split(key).butFirst().key() + "/" + target);
    }

    @Override
    public StorageFile butLast() {
        //TODO:
        return S3StorageFile.get("s3a://" + bucket + "/" + S3KeySplitter.split(key).butLast().key());
    }
}
