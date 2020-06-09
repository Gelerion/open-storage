package com.gelerion.open.storage.api.domain;

public interface StorageDirectory extends StoragePath {
    StorageFile toStorageFile(String fileName);

    StorageDirectory addSubDirectory(String dir);

    String dirName();

    StorageDirectory butLast();
}
