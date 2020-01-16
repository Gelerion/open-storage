package com.gelerion.open.storage.local.test;

import com.gelerion.open.storage.api.Storage;

public class LocalStorageIntegrationTest {
    private static final String STORAGE_PATH = System.getProperty("STORAGE_HOME", System.getProperty("user.home") + "/tmp/trash");

    private static Storage storageService;
}
