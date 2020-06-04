package com.gelerion.open.storage.api.copy;

import com.gelerion.open.storage.api.copy.flow.CopyFlow;
import com.gelerion.open.storage.api.domain.StorageFile;

public class SameStorageCopyTask extends CopyTaskSkeleton {

    public SameStorageCopyTask(CopyFlow.Source source, CopyFlow.Target sink) {
        super(source, sink);
    }

    @Override
    public void execute() {
        source.files().forEach(file -> {
            StorageFile tgtFile = target.resolveTargetPath(file);
            target.storage().create(tgtFile.parentDir()).move(file, tgtFile);
        });
    }
}
