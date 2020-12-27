package com.gelerion.open.storage.local.copy.task;

import com.gelerion.open.storage.api.copy.SameStorageCopyTask;
import com.gelerion.open.storage.api.copy.flow.CopyFlow.Source;
import com.gelerion.open.storage.api.copy.flow.CopyFlow.Target;
import com.gelerion.open.storage.api.copy.spi.CopyTaskProviderSpi;

public class LocalCopyTaskProvider implements CopyTaskProviderSpi {
    @Override
    public String scheme() {
        return "local";
    }

    @Override
    public SameStorageCopyTask createCopyTask(Source source, Target target) {
        return new LocalStorageCopyTask(source, target);
    }
}
