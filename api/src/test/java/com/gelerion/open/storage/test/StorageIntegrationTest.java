package com.gelerion.open.storage.test;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(Lifecycle.PER_CLASS)
public abstract class StorageIntegrationTest {
    Storage storage;

    private final List<StorageFile> filesToDelete = new ArrayList<>();
    private final List<StorageDirectory> dirsToDelete = new ArrayList<>();

    @BeforeAll
    public void init() {
        this.storage = storageImpl();
    }

    @Test
    public void createEmptyFile() throws IOException {
        String fileName = "test.txt";
        StorageFile file = pathToStorageFile(fileName);

        //when
        storage.create(file);
        markForCleanup(file);

        //then
        assertFileExist(file);
        assertFileSizeEqualsTo(file, 0);
    }

    @Test
    public void createEmptyDir() throws IOException {
        String dirName = "test";
        StorageDirectory dir = pathToStorageDir(dirName);

        //when
        storage.create(dir);
        markForCleanup(dir);

        //then
        assertDirExist(dir);
    }

    @AfterEach
    void afterEach() throws IOException {
        for (StorageFile file : filesToDelete) {
            storage.delete(file);
        }

        for (StorageDirectory dir : dirsToDelete) {
            storage.delete(dir);
        }

        filesToDelete.clear();
        dirsToDelete.clear();
    }

    private void markForCleanup(StorageFile file) {
        filesToDelete.add(file);
    }

    private void markForCleanup(StorageDirectory dir) {
        dirsToDelete.add(dir);
    }

    //storage specific methods
    public abstract Storage storageImpl();
    public abstract StorageFile pathToStorageFile(String path);
    public abstract StorageDirectory pathToStorageDir(String path);

    //assertions
    public abstract void assertFileExist(StorageFile file) throws IOException;
    public abstract void assertFileSizeEqualsTo(StorageFile file, long size) throws IOException;
    public abstract void assertFileHasContent(Collection<String> lines) throws IOException;
    public void assertFileHasContent(String line) throws IOException {
        assertFileHasContent(of(line).collect(toList()));
    }

    public abstract void assertDirExist(StorageDirectory dir) throws IOException;
}
