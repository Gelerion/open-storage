package com.gelerion.open.storage.api.domain;

public interface StoragePath extends Comparable<StoragePath> {
    <X> X unwrap(Class<X> clazz);

    StorageDirectory parentDir();

    StoragePath resolve(StoragePath other);
}