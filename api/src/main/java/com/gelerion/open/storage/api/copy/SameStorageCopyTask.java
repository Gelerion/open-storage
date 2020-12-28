package com.gelerion.open.storage.api.copy;

import com.gelerion.open.storage.api.copy.flow.CopyFlow;
import com.gelerion.open.storage.api.domain.StorageFile;

import java.util.function.Consumer;

public abstract class SameStorageCopyTask extends CopyTaskSkeleton {
    private final Consumer<StorageFile> deleteFile = file -> source.storage().delete(file);

    public SameStorageCopyTask(CopyFlow.Source source, CopyFlow.Target sink) {
        super(source, sink);
    }
}
