package com.gelerion.open.storage.api.copy;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.copy.flow.CopyFlow;
import com.gelerion.open.storage.api.copy.flow.CopyFrom;

public class CopyTasks {

    public static CopyFrom newCopyTask() {
        return new CopyFlow();
    }

    public static CopyFrom newCopyTask(Storage storage) {
        return new CopyFlow(storage);
    }
}
