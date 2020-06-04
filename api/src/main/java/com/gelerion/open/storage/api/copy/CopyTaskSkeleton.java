package com.gelerion.open.storage.api.copy;

import com.gelerion.open.storage.api.copy.flow.CopyFlow;
import com.gelerion.open.storage.api.domain.StorageFile;

public abstract class CopyTaskSkeleton implements CopyTask {
    protected final CopyFlow.Source source;
    protected final CopyFlow.Target target;

    public CopyTaskSkeleton(CopyFlow.Source source, CopyFlow.Target target) {
        this.source = source;
        this.target = target;
    }

/*    @Override
    public abstract void execute() {
        //TODO: different CopyTask implementations
        //TODO: remove existing
        //e.g. Same src and target storage -> should be done with native fs api
        //copy between storages require usage readers/writers api

        if (source.storage().name().equals(target.storage().name())) {
            //push down to the native copy api
            source.files().forEach(srcFile -> {
                StorageFile tgtFile = target.resolveTargetPath(srcFile);
                target.storage().create(tgtFile.parentDir()).move(srcFile, tgtFile);
            });
        }
    }*/
}
