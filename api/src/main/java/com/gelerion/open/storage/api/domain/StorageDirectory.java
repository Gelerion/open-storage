package com.gelerion.open.storage.api.domain;

//TODO: generify to local???
public interface StorageDirectory extends StoragePath<StorageDirectory> {
    StorageFile toStorageFile(String fileName);

    StorageDirectory addSubDirectory(String dir);

    String dirName();

    StorageDirectory butLast();

//    @Override
//    StorageDirectory rename(String target);

//    @Override
//    StorageDirectory resolve(StorageDirectory that);
//
//    @Override
//    StorageDirectory resolve(StorageFile that);
}
