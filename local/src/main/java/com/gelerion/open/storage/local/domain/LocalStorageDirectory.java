package com.gelerion.open.storage.local.domain;

import java.nio.file.Path;

public class LocalStorageDirectory extends LocalStoragePath {
    protected LocalStorageDirectory(Path path) {
        super(path);
    }
}
