package com.gelerion.open.storage.local.writer;

import com.gelerion.open.storage.api.domain.StoragePath;
import com.gelerion.open.storage.api.exceptions.NotImplementedException;
import com.gelerion.open.storage.api.ops.StorageOperations.VoidExceptional;
import com.gelerion.open.storage.api.writer.StorageWriter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Stream;

import static com.gelerion.open.storage.api.ops.StorageOperations.run;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.*;

public class LocalStorageWriter implements StorageWriter {

    private final Path path;

    private LocalStorageWriter(Path path) {
        this.path = path;
    }

    public static LocalStorageWriter output(StoragePath output) {
        return new LocalStorageWriter(output.unwrap(Path.class));
    }

    @Override
    public void write(String content) {
        write(Stream.of(content));
    }

    @Override
    public void write(Stream<String> content) {
        run(() -> createPath().andThen(() ->
                Files.write(path, (Iterable<String>) content::iterator, UTF_8, CREATE, TRUNCATE_EXISTING)));
    }

    @Override
    public void write(Collection<String> content) {
        write(content.stream());
    }

    @Override
    public void write(byte[] content) {
        throw new NotImplementedException("TODO");
    }

    @Override
    public void append(Stream<String> content) {
        run(() -> createPath().andThen(() ->
                Files.write(path, (Iterable<String>) content::iterator, UTF_8, CREATE, APPEND)));
    }

    @Override
    public void append(Collection<String> content) {
        append(content.stream());
    }

    private VoidExceptional createPath() {
        return () -> {
            Path parent = path.getParent();
            boolean isRelative = parent == null;

            if (!isRelative && !Files.exists(parent)) {
                Files.createDirectories(path.getParent());
            }
        };
    }
}
