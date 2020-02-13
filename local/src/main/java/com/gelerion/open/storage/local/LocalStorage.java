package com.gelerion.open.storage.local;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StoragePath;
import com.gelerion.open.storage.api.reader.StorageReader;
import com.gelerion.open.storage.api.writer.StorageWriter;
import com.gelerion.open.storage.local.reader.LocalStorageReader;
import com.gelerion.open.storage.local.writer.LocalStorageWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.gelerion.open.storage.api.ops.StorageOperations.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Collections.reverseOrder;

public class LocalStorage implements Storage {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static Storage newLocalStorage() {
        return new LocalStorage();
    }

    @Override
    public Storage create(StorageDirectory folder) {
        return exec(() -> {
            Path path = folder.unwrap(Path.class);
            if (!Files.exists(path)) Files.createDirectories(path);
            return this;
        });
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void delete(StorageDirectory folder) {
        run(() -> {
            Path path = unwrapped(folder);

            //TODO: handle delete false
            Files.walk(path)
                .sorted(reverseOrder())
                .map(Path::toFile)
                .map(File::delete)
                .anyMatch(x -> false); //deletion gone wrong
        });
    }

    @Override
    public void delete(StorageFile file) {
        delete(unwrapped(file));
    }

    @Override
    public long size(StorageDirectory folder) {
        return exec(() -> {
            Predicate<Path> isDirectory = Files::isDirectory;
            return Files.walk(unwrapped(folder))
                    .filter(isDirectory.negate())
                    .mapToLong(path -> execOpt(() -> Files.size(path)).orElse(0))
                    .sum();
        });
    }

    @Override
    public long size(StorageFile file) {
        return execOpt(() -> Files.size(unwrapped(file))).orElse(0);
    }

    @Override
    public StorageReader reader(StorageFile file) {
        return LocalStorageReader.from(file);
    }

    @Override
    public StorageWriter writer(StorageFile file) {
        return LocalStorageWriter.output(file);
    }

    @Override
    public boolean exists(StoragePath path) {
        return Files.exists(unwrapped(path));
    }

    @Override
    public void rename(StorageDirectory source, StorageDirectory target) {
        run(() -> Files.move(unwrapped(source), unwrapped(target), REPLACE_EXISTING));
    }

    @Override
    public void rename(StorageFile source, StorageFile target) {
        run(() -> {
            Path src = unwrapped(source);
            Files.move(src, src.resolveSibling(unwrapped(target)), REPLACE_EXISTING);
        });
    }

    @Override
    public void copy(StoragePath source, StoragePath target) {

    }

    @Override
    public Set<StorageFile> files(StorageDirectory underFolder) {
        return null;
    }

    @Override
    public Stream<StorageDirectory> subFolders(StorageDirectory base) {
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

    private Path unwrapped(StoragePath file) {
        return file.unwrap(Path.class);
    }

    private void delete(Path path) {
        run(() -> Files.deleteIfExists(path));
    }
}
