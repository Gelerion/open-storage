package com.gelerion.open.storage.local;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.local.domain.LocalStorageDirectory;
import com.gelerion.open.storage.local.domain.LocalStorageFile;

public class Main {

    public static void main(String[] args) {
        StorageFile file = LocalStorageFile.get("abc");
        LocalStorageDirectory directory = new LocalStorageDirectory();

        Storage storage = LocalStorage.newLocalStorage();

        storage.delete(file);
    }

}
