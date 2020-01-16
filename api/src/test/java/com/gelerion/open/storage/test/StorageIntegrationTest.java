package com.gelerion.open.storage.test;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.domain.StorageFile;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class StorageIntegrationTest {
    Storage storage;

    @Test
    public void createNewFile() {
        String fileName = "test.txt";
//        StorageFile file = LocalStorageFile.file(fileName);
//        storage.writer(file).write(Stream.of("Hello world!", "What a perfect day!"));

//        Path created = Paths.get(STORAGE_PATH, fileName);
//        assertTrue(Files.exists(created));
    }

}
