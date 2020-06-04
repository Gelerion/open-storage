package com.gelerion.open.storage.api.copy;

import com.gelerion.open.storage.api.copy.flow.CopyFlow;

public class DifferentStoragesCopyTask extends CopyTaskSkeleton {

    public DifferentStoragesCopyTask(CopyFlow.Source source, CopyFlow.Target target) {
        super(source, target);
    }

    @Override
    public void execute() {

    }
}
