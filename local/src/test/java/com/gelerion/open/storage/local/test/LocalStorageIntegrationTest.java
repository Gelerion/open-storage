package com.gelerion.open.storage.local.test;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.local.LocalStorage;
import com.gelerion.open.storage.local.domain.LocalStorageFile;
import com.gelerion.open.storage.test.StorageIntegrationTest;

public class LocalStorageIntegrationTest extends StorageIntegrationTest {
    private static final String STORAGE_PATH = System.getProperty("STORAGE_HOME", System.getProperty("user.home") + "/tmp/trash");

    private static Storage storageService;

    @Override
    public Storage storageImpl() {
        return LocalStorage.newLocalStorage();
    }

    @Override
    public StorageFile createFile(String path) {
        return LocalStorageFile.get(path);
    }
}
