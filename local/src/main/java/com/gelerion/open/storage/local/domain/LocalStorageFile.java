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
        Path resolved = currentPath.resolve(file.asString());
        return LocalStorageFile.get(resolved);
    }

    @Override
    public LocalStorageDirectory resolve(LocalStorageDirectory folder) {
        Path resolved = currentPath.resolve(folder.asString());
        return LocalStorageDirectory.get(resolved);
    }

    @Override
    public String fileName() {
        return currentPath.getFileName().toString();
    }
}
