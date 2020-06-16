package com.gelerion.open.storage.api.copy;

import com.gelerion.open.storage.api.copy.flow.CopyFlow;

public class ForeignStorageCopyTask extends CopyTaskSkeleton {

    public ForeignStorageCopyTask(CopyFlow.Source source, CopyFlow.Target target) {
        super(source, target);
    }

    @Override
    public void execute() {

    }
}
