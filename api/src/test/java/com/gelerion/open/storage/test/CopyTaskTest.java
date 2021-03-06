package com.gelerion.open.storage.test;

import org.junit.jupiter.api.Test;


public class CopyTaskTest {


    @Test
    void fluentApiUsage() {
        //all the tedious work such as:
        //  - resolving paths
        //  - handling inter storage communication
        //  - network failures
        //  - taking care of splitting big files into small chunks and parallelism
        //  - asynchronous execution
        //is handled for you by the library.
        //Following convention over configuration approach reasonable defaults were chosen, tough they are fully configurable.

//        CopyTasks.newCopyTask()
//                .from(FromSpec.file().map((storage, file) -> ))
//                .target()
//                .withSourceListener()

//        CopyTasks.newCopyTask()
//                .from(file(mockStorageFile)
//                        .map((st, f) -> st.rename(f, f.rename("new-name")))
//                        .filter(src -> src.fileName().startsWith("abc")));

        //.source(path(storageFile).withListener((storage, file) -> log.info(file)))
//        CopyTasks.newCopyTask()
//                //.source(s3, file)
//                .from(mockStorage, mockStorageFile)
//                .to(mockStorage, mockStorageFile)
//                .copy();
    }

}
