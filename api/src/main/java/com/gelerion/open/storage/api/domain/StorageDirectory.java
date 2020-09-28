package com.gelerion.open.storage.api.domain;

public interface StorageDirectory extends StoragePath<StorageDirectory> {
    StorageFile toStorageFile(String fileName);

    StorageDirectory addSubDirectory(String dir);

    String dirName();

    StorageDirectory butLast();

    @Override
    default String name() {
        return dirName();
    }
}
