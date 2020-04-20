package com.gelerion.open.storage.test;

import com.gelerion.open.storage.api.copy.CopyTask;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.domain.StoragePath;
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

        CopyTask.newCopyTask()
                .source(mockStorageFile)
                .target(mockStorageFile)
                .copy();
    }




    private final StorageFile mockStorageFile = new StorageFile() {
        @Override
        public int compareTo(StoragePath o) {
            return 0;
        }

        @Override
        public <X> X unwrap(Class<X> clazz) {
            return null;
        }

        @Override
        public StorageDirectory parentDir() {
            return null;
        }

        @Override
        public StoragePath resolve(StoragePath other) {
            return null;
        }

        @Override
        public String fileName() {
            return null;
        }
    };
}
