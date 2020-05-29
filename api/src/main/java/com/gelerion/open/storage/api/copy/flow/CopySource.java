package com.gelerion.open.storage.api.copy.flow;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.domain.StoragePath;

//https://medium.com/xebia-engineering/fluent-builder-pattern-with-a-real-world-example-7b61be375a40
public interface CopySource {
    CopyTarget source(Storage storage, StoragePath path);

//    CopySource storage(Storage storage);

    //CopySource path(Copy storage);//\\

}
