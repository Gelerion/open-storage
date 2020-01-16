package com.gelerion.open.storage.local.domain;

import com.gelerion.open.storage.api.domain.StorageFile;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalStorageFile extends LocalStoragePath implements StorageFile {

    public static StorageFile get(String path) {
        return get(Paths.get(path));
    }

    private static LocalStorageFile get(Path path) {
        //TODO: validate invariants aka valid file
        return new LocalStorageFile(path);
    }

    private LocalStorageFile(Path path) {
        super(path);
    }
}
