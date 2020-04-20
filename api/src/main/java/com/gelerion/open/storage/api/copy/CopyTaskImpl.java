package com.gelerion.open.storage.api.copy;

import com.gelerion.open.storage.api.copy.flow.CopyFlow;

public class CopyTaskImpl implements CopyTask {
    private final CopyFlow flow;

    public CopyTaskImpl(CopyFlow flow) {
        this.flow = flow;
    }

    @Override
    public CopyTask options() {
        return null;
    }

    @Override
    public void copy() {

    }
}
