package com.gelerion.open.storage.api.rename;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StoragePath;

import java.util.function.Function;

//TODO: generify
public final class DirectoryRenamer implements Renamer<StorageDirectory> {
    private final StorageDirectory source;
    private final Storage storage;

    public DirectoryRenamer(StorageDirectory source, Storage storage) {
        this.source = source;
        this.storage = storage;
    }

    @Override
    public StorageDirectory to(String target) {
        return null;
    }

    @Override
    public StorageDirectory to(StorageDirectory target) {
        return storage.move(source, source.rename(target.dirName()));
    }

    @Override
    public StorageDirectory to(Function<StorageDirectory, StorageDirectory> func) {
        return null;
    }

//    @Override
//    public void to(String target) {
////        final StoragePath<?> abc = source.rename("abc");
//        storage.move(source, source.rename("abc"));
//    }
//
//    @Override
//    public void to(StoragePath<?> target) {
//
//    }
//
//    @Override
//    public void to(Function<StoragePath<?>, StoragePath<?>> func) {
//
//    }
}
