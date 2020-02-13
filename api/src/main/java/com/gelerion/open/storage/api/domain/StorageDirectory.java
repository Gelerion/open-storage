package com.gelerion.open.storage.api.domain;

public interface StorageDirectory extends StoragePath {
    StorageFile toStorageFile(String fileName);
}
