package com.gelerion.open.storage.api.copy.flow;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.copy.CopyTask;
import com.gelerion.open.storage.api.domain.StoragePath;

public interface CopyTarget {

    CopyTask target(Storage storage, StoragePath path);

}
