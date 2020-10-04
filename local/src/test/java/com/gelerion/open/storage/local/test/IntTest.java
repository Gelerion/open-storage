package com.gelerion.open.storage.local.test;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.local.LocalStorage;
import com.gelerion.open.storage.local.domain.LocalStorageFile;
import com.gelerion.open.storage.test.StorageIntegrationTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntTest extends StorageIntegrationTest {
    private final Storage storage = LocalStorage.newLocalStorage();

    @Override
    public Storage storageImpl() {
        return storage;
    }

    @Override
    public StorageFile pathToStorageFile(String path) {
        return LocalStorageFile.get(path);
    }

    @Override
    public StorageDirectory pathToStorageDir(String s) {
        return null;
    }

    @Override
    public void assertFileExist(StorageFile file) throws IOException {
        Path path = file.unwrap(Path.class);
        assertTrue(Files.exists(path));
    }

    @Override
    public void assertFileSizeEqualsTo(StorageFile file, long size) throws IOException {
        Path path = file.unwrap(Path.class);
        assertEquals(size, Files.size(path));
    }

    @Override
    public void assertFileHasContent(Collection<String> collection) {

    }

    @Override
    public void assertDirExist(StorageDirectory dir) throws IOException {
        assertTrue(Files.exists(dir.unwrap(Path.class)));
    }
}
