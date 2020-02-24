package com.gelerion.open.storage.local;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.domain.StoragePath;
import com.gelerion.open.storage.api.reader.StorageReader;
import com.gelerion.open.storage.api.writer.StorageWriter;
import com.gelerion.open.storage.local.domain.LocalStorageFile;
import com.gelerion.open.storage.local.reader.LocalStorageReader;
import com.gelerion.open.storage.local.writer.LocalStorageWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.gelerion.open.storage.api.ops.StorageOperations.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Collections.reverseOrder;
import static java.util.stream.Collectors.toSet;

public class LocalStorage implements Storage {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static Storage newLocalStorage() {
        return new LocalStorage();
    }

    @Override
    public Storage create(StorageDirectory dir) {
        return exec(() -> {
            Path path = dir.unwrap(Path.class);
            if (!Files.exists(path)) Files.createDirectories(path);
            return this;
        });
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void delete(StorageDirectory dir) {
        run(() -> {
            Path path = unwrapped(dir);

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
    public long size(StorageDirectory dir) {
        return exec(() -> {
            Predicate<Path> isDirectory = Files::isDirectory;
            return Files.walk(unwrapped(dir))
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

//    @Override
//    public void copy(StoragePath source, StoragePath target) {
//
//    }

    @Override
    public Set<StorageFile> files(StorageDirectory underDir) {
        Path resolved = underDir.unwrap(Path.class);
        Predicate<Path> isDirectory = Files::isDirectory;

        return exec(() -> {
            //Try with resources is mandatory here, do not remove it!
            try (Stream<Path> stream = Files.list(resolved)) {
                return stream.filter(isDirectory.negate())
                        .map(LocalStorageFile::get)
                        .collect(toSet());
            }
        });
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
