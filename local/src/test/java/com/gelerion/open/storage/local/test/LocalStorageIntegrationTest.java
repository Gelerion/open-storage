package com.gelerion.open.storage.local.test;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.local.LocalStorage;
import com.gelerion.open.storage.local.domain.LocalStorageDirectory;
import com.gelerion.open.storage.local.domain.LocalStorageFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Comparator.reverseOrder;
import static java.util.function.Function.identity;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocalStorageIntegrationTest /*extends StorageIntegrationTest*/ {
    private final Storage storage = LocalStorage.newLocalStorage();

    private final List<Path> filesToDelete = new ArrayList<>();
    private final List<String> dirsToDelete = new ArrayList<>();

    @Test
    public void createNewFile() {
        String fileName = "test.txt";
        StorageFile file = createFile(fileName);
        storage.writer(file).write(Stream.of("Hello world!", "What a perfect day!"));

        Path created = Paths.get(fileName);
        assertTrue(Files.exists(created));
    }

    @Test
    public void createNewFileInsideNonExistingDirectory() {
        String dir = "abc";
        String fileName = "test.txt";
        StorageFile file = createDir(dir).toStorageFile(fileName);
        storage.writer(file).write(Stream.of("Hello world!", "What a perfect day!"));

        Path created = Paths.get(dir, fileName);
        assertTrue(Files.exists(created));
    }

    @Test
    public void directlyCreateNewFileInsideNonExistingDirectory() {
        String dir = "abc";
        dirsToDelete.add(dir);
        String fileName = "test.txt";
        StorageFile locatedAtFile = createFile(Paths.get(dir, fileName));
        storage.writer(locatedAtFile).write(Stream.of("Hello world!"));

        Path created = Paths.get(dir, fileName);
        assertTrue(Files.exists(created));
    }

    @Test
    public void readPreviouslyCreatedFile() {
        StorageFile file = createFile("test.txt");
        List<String> expected = asList("Hello world!", "What a perfect day!");

        storage.writer(file).write(expected.stream());
        List<String> content = storage.reader(file).read();

        assertIterableEquals(expected, content);
    }

    private StorageFile createFile(String path) {
        return createFile(Paths.get(path));
    }

    private StorageFile createFile(Path path) {
        filesToDelete.add(path);
        return LocalStorageFile.get(path);
    }

    private StorageDirectory createDir(String path) {
        dirsToDelete.add(path);
        return LocalStorageDirectory.get(path);
    }

    @AfterEach
    void afterEach() throws IOException {
        for (Path file : filesToDelete) {
            Files.delete(file);
        }

        for (String dir : dirsToDelete) {
            deleteAllFiles(dir);
        }

        filesToDelete.clear();
        dirsToDelete.clear();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void deleteAllFiles(String dir) throws IOException {
        Path storage = Paths.get(dir);
        if (Files.exists(storage)) {
            Stream.of(Files.walk(storage))
                    .flatMap(identity()) //free up the resources
                    .sorted(reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }
}
