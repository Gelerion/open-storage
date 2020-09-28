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
        return workingPath.getFileName().toString();
    }

    //TODO: optimize?
    @Override
    public StorageFile rename(String target) {
        return workingPath.getNameCount() == 1 ?
                LocalStorageFile.get(target) :
                LocalStorageFile.get(workingPath).parentDir().toStorageFile(target);
    }

    @Override
    public StorageFile butLast() {
        return workingPath.getNameCount() == 1 ? this :
                LocalStorageFile.get(workingPath.subpath(1, workingPath.getNameCount()));
    }
}
