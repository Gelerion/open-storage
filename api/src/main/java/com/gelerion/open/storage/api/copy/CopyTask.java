package com.gelerion.open.storage.api.copy;

public interface CopyTask {

    /**
     * Default behaviour:
     *  1. If file already exists - replace
     *  2. Source file(s)/dir(s) are unaffected
     *  3. In case source path is a directory, all sub directories wont be taken into account
     */
    void copy();

    /**
     * non blocking call, managed internally in common thread pool
     */
    //CopyTaskFuture addToCopyQueue();
}
