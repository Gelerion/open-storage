package com.gelerion.open.storage.api.copy.spi;

import com.gelerion.open.storage.api.copy.SameStorageCopyTask;
import com.gelerion.open.storage.api.copy.flow.CopyFlow.Source;
import com.gelerion.open.storage.api.copy.flow.CopyFlow.Target;

public interface CopyTaskProviderSpi {

    String scheme();

    SameStorageCopyTask createCopyTask(Source source, Target target);

}
