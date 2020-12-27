package com.gelerion.open.storage.local.copy.task;

import com.gelerion.open.storage.api.copy.SameStorageCopyTask;
import com.gelerion.open.storage.api.copy.flow.CopyFlow;
import com.gelerion.open.storage.api.domain.StorageFile;

public class LocalStorageCopyTask extends SameStorageCopyTask {

    public LocalStorageCopyTask(CopyFlow.Source source, CopyFlow.Target sink) {
        super(source, sink);
    }

    @Override
    protected void copyFile(StorageFile source, StorageFile target) {
        System.out.println("copy from " + source + " to " + target);
    }

}
