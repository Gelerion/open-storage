package com.gelerion.open.storage.api.rename;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.domain.StorageFile;

public final class FileRenamer extends StoragePathRenamer<StorageFile> {

    public FileRenamer(StorageFile source, Storage storage) {
        super(source, storage);
    }
}
