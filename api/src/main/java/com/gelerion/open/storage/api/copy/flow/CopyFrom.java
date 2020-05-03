package com.gelerion.open.storage.api.copy.flow;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.domain.StoragePath;

public interface CopyFrom {

    CopyTo from(StoragePath path);

    CopyTo from(Storage storage, StoragePath path);

    CopyTo from(FromSpec spec);

    CopyTo from(Storage storage, FromSpec spec);
}


