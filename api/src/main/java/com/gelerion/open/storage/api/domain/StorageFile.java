package com.gelerion.open.storage.api.domain;

public interface StorageFile extends StoragePath<StorageFile> {
    String fileName();

    @Override
    default String name() {
        return fileName();
    }

}
