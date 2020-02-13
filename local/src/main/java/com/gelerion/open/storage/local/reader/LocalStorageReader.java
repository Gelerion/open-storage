package com.gelerion.open.storage.local.reader;

import com.gelerion.open.storage.api.domain.StoragePath;
import com.gelerion.open.storage.api.reader.StorageReaderSkeleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.GZIPInputStream;

public class LocalStorageReader extends StorageReaderSkeleton {
    protected static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Path path;

    public LocalStorageReader(Path output) {
        this.path = output;
    }

    public static LocalStorageReader from(StoragePath input) {
        return new LocalStorageReader(input.unwrap(Path.class));
    }

    @Override
    protected Stream<String> rawContent() {
        try {
            if (!Files.exists(path)) {
                log.warn("File does not exist {}", path);
                return Stream.empty();
            }

            if (Files.isDirectory(path)) {
                return StreamSupport.stream(Files.newDirectoryStream(path).spliterator(), false)
                        .filter(LocalStorageReader::isNotDirectory)
                        .flatMap(this::lines);
            }

            return lines(path);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void close() {
        //nothing to close
    }

    private Stream<String> lines(Path path) {
        try {
            //we could check an actual type with Files.probeContentType(path)
            //but lets do it in easy way :)
            if (isZipped) {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        new GZIPInputStream(Files.newInputStream(path)), charset));
                return br.lines().onClose(asUncheckedRunnable(br));
            }

            return Files.lines(path);
        }
        catch (IOException e) {
            log.warn("File is unreadable {}", path);
            return Stream.empty();
        }
    }

    private static boolean isNotDirectory(Path path) {
        if (Files.isDirectory(path)) {
            log.warn("Recursive option not allowed, omitting sub directory {}", path);
            return false;
        }
        return true;
    }
}
