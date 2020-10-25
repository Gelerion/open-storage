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
import java.util.Set;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@TestInstance(Lifecycle.PER_CLASS)
public abstract class StorageIntegrationTest {
    private static final int LINE_SEP_SIZE = System.getProperty("line.separator").getBytes(UTF_8).length;

    protected Storage storage;

    private final List<StorageFile> filesToDelete = new ArrayList<>();
    private final List<StorageDirectory> dirsToDelete = new ArrayList<>();

    @BeforeAll
    protected void init() {
        this.storage = storageImpl();
    }

    @Test
    public void createEmptyFile() throws IOException {
        String fileName = "empty-test.txt";
        StorageFile file = createStorageFile(fileName);

        //when
        storage.create(file);

        //then
        assertFileExist(file);
        assertFileSizeEqualsTo(file, 0);
    }

    @Test
    public void createEmptyDir() throws IOException {
        String dirName = "test";
        StorageDirectory dir = createStorageDir(dirName);

        //when
        storage.create(dir);
        markForCleanup(dir);

        //then
        assertDirExist(dir);
    }

    @Test
    public void createFile() throws IOException {
        String fileName = "test.txt";
        StorageFile file = createStorageFile(fileName);
        Set<String> content = Stream.of("Hello world!", "What a perfect day!").collect(toSet());
        storage.writer(file).write(content);

        assertFileExist(file);
        assertFileSizeEqualsTo(file, calcContentSize(content));
    }

    @Test
    public void createNewFileInsideNonExistingDirectory() throws IOException {
        String dir = "abc";
        String fileName = "test.txt";
        StorageDirectory storageDir = createStorageDir(dir);
        StorageFile file = storageDir.toStorageFile(fileName);

        Set<String> content = Stream.of("Hello world!", "What a perfect day!").collect(toSet());
        storage.writer(file).write(content);

        assertFileExist(file);
        assertDirExist(storageDir);
        assertFileSizeEqualsTo(file, calcContentSize(content));
    }

    @AfterEach
    protected void afterEach() throws IOException {
        for (StorageFile file : filesToDelete) {
            deleteFile(file);
        }

        for (StorageDirectory dir : dirsToDelete) {
            deleteDir(dir);
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

    private StorageFile createStorageFile(String fileName) {
        StorageFile result = pathToStorageFile(fileName);
        filesToDelete.add(result);
        return result;
    }

    private StorageDirectory createStorageDir(String path) {
        StorageDirectory result = pathToStorageDir(path);
        dirsToDelete.add(result);
        return result;
    }

    private int calcContentSize(Collection<String> content) {
        //line separator is added after each line
        int separatorsSize = content.size() * LINE_SEP_SIZE;
        return content.stream()
                .map(line -> line.getBytes(UTF_8).length)
                .reduce(0, Integer::sum) + separatorsSize;
    }

    //storage specific methods
    protected abstract Storage storageImpl();
    protected abstract StorageFile pathToStorageFile(String path);
    protected abstract StorageDirectory pathToStorageDir(String path);

    protected abstract void deleteFile(StorageFile file) throws IOException;
    protected abstract void deleteDir(StorageDirectory dir) throws IOException;

    //assertions
    protected abstract void assertFileExist(StorageFile file) throws IOException;
    protected abstract void assertFileSizeEqualsTo(StorageFile file, long size) throws IOException;
    protected abstract void assertFileHasContent(Collection<String> lines) throws IOException;
    protected void assertFileHasContent(String line) throws IOException {
        assertFileHasContent(Stream.of(line).collect(toList()));
    }

    protected abstract void assertDirExist(StorageDirectory dir) throws IOException;
}
