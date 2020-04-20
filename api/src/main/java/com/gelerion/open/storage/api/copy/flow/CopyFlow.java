package com.gelerion.open.storage.api.copy.flow;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.copy.CopyTask;
import com.gelerion.open.storage.api.copy.CopyTaskImpl;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.domain.StoragePath;

import java.util.Collection;
import java.util.stream.Stream;

public class CopyFlow {

    public CopyFlow source(StoragePath src) {
        return this;
    }

    public CopyTask target(StoragePath dst) {
        return new CopyTaskImpl(this);
    }




}
