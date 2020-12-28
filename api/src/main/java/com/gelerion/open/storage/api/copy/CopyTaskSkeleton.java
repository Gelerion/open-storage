package com.gelerion.open.storage.api.copy;

import com.gelerion.open.storage.api.copy.flow.CopyFlow.Source;
import com.gelerion.open.storage.api.copy.flow.CopyFlow.Target;
import com.gelerion.open.storage.api.copy.options.StorageCopyOption;

public abstract class CopyTaskSkeleton implements CopyTask {
    protected final Source source;
    protected final Target target;
    protected StorageCopyOption[] copyOptions;

    public CopyTaskSkeleton(Source source, Target target) {
        this.source = source;
        this.target = target;
        this.copyOptions = new StorageCopyOption[]{};
    }

    @Override
    public CopyTask options(StorageCopyOption... options) {
        this.copyOptions = options;
        return this;
    }

    protected boolean hasOption(StorageCopyOption option) {
        if (this.copyOptions.length == 0) return false;
        for (StorageCopyOption copyOption : this.copyOptions) {
            if (copyOption == option) return true;
        }
        return false;
    }

    //TODO: [logic] add validation, e.g. make sure we don't allow coping the files to the same directory

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
