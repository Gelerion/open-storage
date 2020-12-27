package com.gelerion.open.storage.api.copy;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.copy.flow.CopyFlow;
import com.gelerion.open.storage.api.copy.flow.CopySource;
import com.gelerion.open.storage.api.copy.options.StorageCopyOption;

public interface CopyTask {

    static CopySource newCopyTask() {
        return new CopyFlow();
    }

    static CopySource newCopyTask(Storage storage) {
        return new CopyFlow(storage);
    }

    /**
     * Default behaviour:
     *  1. If file already exists - replace
     *  2. Source file(s)/dir(s) are unaffected
     *  3. In case source path is a directory, all sub directories wont be taken into account
     */
    void execute();

    CopyTask options(StorageCopyOption... options);

    //dryRun

    /**
     * non blocking call, managed internally in a common thread pool
     */
    //CopyTaskFuture addToCopyQueue();
}
