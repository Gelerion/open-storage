package com.gelerion.open.storage.test;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.copy.CopyTask;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.domain.StoragePath;
import com.gelerion.open.storage.api.reader.StorageReader;
import com.gelerion.open.storage.api.writer.StorageWriter;
import org.junit.jupiter.api.Test;

import java.util.Set;

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
                .source(mockStorage, mockStorageFile)
                .target(mockStorage, mockStorageFile)
                .copy();
    }


    private final Storage mockStorage = new Storage() {
        @Override
        public Storage create(StorageDirectory folder) {
            return null;
        }

        @Override
        public void delete(StorageDirectory folder) {

        }

        @Override
        public void delete(StorageFile file) {

        }

        @Override
        public long size(StorageDirectory folder) {
            return 0;
        }

        @Override
        public long size(StorageFile file) {
            return 0;
        }

        @Override
        public StorageReader reader(StorageFile file) {
            return null;
        }

        @Override
        public StorageWriter writer(StorageFile file) {
            return null;
        }

        @Override
        public boolean exists(StoragePath path) {
            return false;
        }

        @Override
        public void rename(StorageDirectory source, StorageDirectory target) {

        }

        @Override
        public void rename(StorageFile source, StorageFile target) {

        }

        @Override
        public Set<StorageFile> files(StorageDirectory underDir) {
            return null;
        }

        @Override
        public Set<StorageDirectory> dirs(StorageDirectory underDir) {
            return null;
        }

        @Override
        public StorageDirectory fullPath(StorageDirectory folder) {
            return null;
        }

        @Override
        public StorageFile fullPath(StorageFile file) {
            return null;
        }
    };

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
