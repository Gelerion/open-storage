package com.gelerion.open.storage.api.copy.flow;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.domain.StoragePath;

public interface CopySource {

    CopyTarget source(StoragePath path);

    CopyTarget source(Storage storage, StoragePath path);

    CopyTarget source(SourceSpec spec);

    CopyTarget source(Storage storage, SourceSpec spec);
}


