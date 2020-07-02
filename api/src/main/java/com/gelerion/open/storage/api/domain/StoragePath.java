package com.gelerion.open.storage.api.domain;

public interface StoragePath<T extends StoragePath<T>> extends Comparable<StoragePath<T>> {
    <X> X unwrap(Class<X> clazz);

    StorageDirectory parentDir();

    <X extends StoragePath<?>> X resolve(X that);

    T rename(String target);

    //return the last name
    // e.g a/b/c/file.txt -> file.txt
    // e.g a/b/c/ -> c
    String name();

    //return path with first element removed or file itself
    // a/b/c/file.txt -> b/c/file.txt
    T butLast();
}