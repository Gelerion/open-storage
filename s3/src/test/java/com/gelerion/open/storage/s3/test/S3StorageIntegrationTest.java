package com.gelerion.open.storage.s3.test;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.s3.S3Storage;
import com.gelerion.open.storage.test.StorageIntegrationTest;

import java.io.IOException;
import java.util.Collection;

public class S3StorageIntegrationTest extends StorageIntegrationTest {

    @Override
    public Storage storageImpl() {
        return S3Storage.newS3Storage();
    }

    @Override
    public StorageFile pathToStorageFile(String file) {
        return null;
    }

    @Override
    public StorageDirectory pathToStorageDir(String dir) {
        return null;
    }

    @Override
    public void assertFileExist(StorageFile file) throws IOException {

    }

    @Override
    public void assertFileSizeEqualsTo(StorageFile storageFile, long size) throws IOException {

    }

    @Override
    public void assertFileHasContent(Collection<String> lines) throws IOException {

    }

    @Override
    public void assertDirExist(StorageDirectory dir) throws IOException {

    }
}
