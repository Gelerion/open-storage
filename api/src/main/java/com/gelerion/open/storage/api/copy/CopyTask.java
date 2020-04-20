package com.gelerion.open.storage.api.copy;

import com.gelerion.open.storage.api.copy.flow.CopyFlow;
import com.gelerion.open.storage.api.domain.StoragePath;

public interface CopyTask {

    static CopyFlow newCopyTask() {
        return new CopyFlow();
    }

    CopyTask options();

    /**
     * The defaults for convention over configuration are:
     *  1. replace existing file
     *  2. source file(s)/dir(s) stays untouched
     *  3. doesn't visit directories recursively
     */
    void copy();

    // non blocking call, copied internally sometime in the future
    //CopyTaskFuture addToCopyQueue();

//    CopyTask source(StoragePath src);
//
//    CopyTask target(StoragePath dst);


}
