package com.gelerion.open.storage.api.rename;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.domain.StorageDirectory;

public final class DirectoryRenamer extends StoragePathRenamer<StorageDirectory> {

    public DirectoryRenamer(StorageDirectory source, Storage storage) {
        super(source, storage);
    }
}
