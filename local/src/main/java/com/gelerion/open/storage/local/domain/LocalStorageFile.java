package com.gelerion.open.storage.local.domain;

import com.gelerion.open.storage.api.domain.StorageFile;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalStorageFile extends LocalStoragePath implements StorageFile {

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
    public StorageFile rename(String newName) {
        return LocalStorageFile.get(currentPath).parentDir().toStorageFile(newName);
    }
}
