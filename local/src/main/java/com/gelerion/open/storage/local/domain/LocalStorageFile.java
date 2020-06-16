package com.gelerion.open.storage.local.domain;

import com.gelerion.open.storage.api.domain.StorageFile;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalStorageFile extends LocalStoragePath<StorageFile> implements StorageFile {

    public static LocalStorageFile get(String path) {
        return get(Paths.get(path));
    }

    public static LocalStorageFile get(Path path) {
        return new LocalStorageFile(path);
    }

    private LocalStorageFile(Path path) {
        super(path);
    }

    @Override
    public LocalStorageFile resolve(LocalStorageFile file) {
        return parentDir().resolve(file);
    }

    @Override
    public LocalStorageDirectory resolve(LocalStorageDirectory dir) {
        return parentDir().resolve(dir);
    }

    @Override
    public String fileName() {
        return currentPath.getFileName().toString();
    }

    //TODO: optimize?
    @Override
    public StorageFile rename(String target) {
        return currentPath.getNameCount() == 1 ?
                LocalStorageFile.get(target) :
                LocalStorageFile.get(currentPath).parentDir().toStorageFile(target);
    }

    @Override
    public StorageFile butLast() {
        return currentPath.getNameCount() == 1 ? this :
                LocalStorageFile.get(currentPath.subpath(1, currentPath.getNameCount()));
    }
}
