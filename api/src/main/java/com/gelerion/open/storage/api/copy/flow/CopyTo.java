package com.gelerion.open.storage.api.copy.flow;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.copy.CopyTask;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StoragePath;

public interface CopyTo {

    CopyTask to(StorageDirectory dir);

    CopyTask to(Storage storage, StorageDirectory dir);

    CopyTask to(ToSpec spec);

    CopyTask to(Storage storage, ToSpec spec);

}
