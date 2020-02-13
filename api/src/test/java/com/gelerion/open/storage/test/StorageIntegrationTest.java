package com.gelerion.open.storage.test;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.domain.StorageFile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(Lifecycle.PER_CLASS)
public abstract class StorageIntegrationTest {
    Storage storage;

    @BeforeAll
    public void init() {
        this.storage = storageImpl();
    }

    @Test
    public void createNewFile() {
        String fileName = "test.txt";
        StorageFile file = createFile(fileName);
        storage.writer(file).write(Stream.of("Hello world!", "What a perfect day!"));

        Path created = Paths.get(fileName);
        assertTrue(Files.exists(created));
    }

    public abstract Storage storageImpl();
    public abstract StorageFile createFile(String path);

}
