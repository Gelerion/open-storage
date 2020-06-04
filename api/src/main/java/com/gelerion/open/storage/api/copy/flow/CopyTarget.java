package com.gelerion.open.storage.api.copy.flow;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.copy.CopyTask;
import com.gelerion.open.storage.api.domain.StorageDirectory;

public interface CopyTarget {

    CopyTask target(StorageDirectory dir);

    CopyTask target(Storage storage, StorageDirectory dir);

    CopyTask target(TargetSpec spec);

    CopyTask target(Storage storage, TargetSpec spec);

}
