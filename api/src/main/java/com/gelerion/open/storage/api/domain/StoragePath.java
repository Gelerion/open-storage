package com.gelerion.open.storage.api.domain;

public interface StoragePath extends Comparable<StoragePath> {
    <X> X unwrap(Class<X> clazz);

    StorageDirectory parentDir();

    default StoragePath resolve(StoragePath other) {
        return other instanceof StorageDirectory ?
                resolve((StorageDirectory) other) :
                resolve((StorageFile) other);
    }

    StorageFile resolve(StorageFile file);

    StorageDirectory resolve(StorageDirectory dir);

    //return path with first element removed or file itself
    // a/b/c/file.txt -> b/c/file.txt
    StoragePath butLast();
}