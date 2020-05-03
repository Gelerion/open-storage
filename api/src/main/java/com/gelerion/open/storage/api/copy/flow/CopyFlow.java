package com.gelerion.open.storage.api.copy.flow;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.copy.CopyTask;
import com.gelerion.open.storage.api.copy.CopyTaskImpl;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.domain.StoragePath;

import java.util.Collection;
import java.util.stream.Stream;

public class CopyFlow implements CopySource, CopyTarget {


    @Override
    public CopyTarget source(Storage storage, StoragePath path) {
        return null;
    }

    @Override
    public CopyTask target(Storage storage, StoragePath path) {
        return null;
    }
}
