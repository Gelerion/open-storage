package com.gelerion.open.storage.local;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.copy.CopyTask;
import com.gelerion.open.storage.api.copy.flow.CopySource;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.domain.StoragePath;
import com.gelerion.open.storage.api.ops.ListFilesOption;
import com.gelerion.open.storage.api.reader.StorageReader;
import com.gelerion.open.storage.api.writer.StorageWriter;
import com.gelerion.open.storage.local.domain.LocalStorageDirectory;
import com.gelerion.open.storage.local.domain.LocalStorageFile;
import com.gelerion.open.storage.local.reader.LocalStorageReader;
import com.gelerion.open.storage.local.writer.LocalStorageWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
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
    public String name() {
        return "local";
    }

    @Override
    public Storage create(StorageDirectory directory) {
        return exec(() -> createInternal(directory));
    }

    private Storage createInternal(StorageDirectory directory) throws IOException {
        Path path = directory.unwrap(Path.class);
        if (!Files.exists(path)) Files.createDirectories(path);
        return this;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void delete(StorageDirectory directory) {
        run(() -> {
            Path path = unwrapped(directory);

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
    public StorageFile renameFile(StorageFile source, StorageFile target) {
        return move(source, source.rename(target.fileName()));
    }

    @Override
    public StorageFile renameDir(StorageDirectory source, StorageDirectory target) {
        return move(source, source.parentDir().addSubDirectory(target.dirName()));
    }

    @Override
    public StorageFile move(StorageDirectory source, StorageDirectory target) {
        return exec(() ->
                LocalStorageFile.get(Files.move(
                        unwrapped(source),
                        unwrapped(source.resolve(target)), REPLACE_EXISTING)
                )
        );
    }

    @Override
    public StorageFile move(StorageFile source, StorageFile target) {
        return exec(() -> {
            final StorageFile resolved = source.resolve(target);
            createInternal(resolved.parentDir());
            return LocalStorageFile.get(Files.move(
                    unwrapped(source),
                    unwrapped(resolved), REPLACE_EXISTING));
            });
    }

    @Override
    public void copy(StoragePath source, StoragePath target) {
//        Files.copy()
    }

    @Override
    public CopySource copy() {
        return CopyTask.newCopyTask(this);
    }

//    @Override
//    public void copy(StoragePath source, StoragePath target) {
//
//    }

    @Override
    public Set<StorageFile> files(StorageDirectory underDir, ListFilesOption... opts) {
        Path start = underDir.unwrap(Path.class);
        return exec(() -> {
            //Try with resources is mandatory here, do not remove it!
            try (Stream<Path> stream = recursively(opts) ? Files.walk(start) : Files.list(start)) {
                return collectFiles(stream).collect(toSet());
            }});
    }

    private boolean recursively(ListFilesOption... opts) {
        if (opts.length <= 0) return false;
        for (ListFilesOption opt : opts) {
            if (opt == ListFilesOption.RECURSIVELY) return true;
        }
        return false;
    }

    private Stream<StorageFile> collectFiles(Stream<Path> stream) {
        Predicate<Path> isDirectory = Files::isDirectory;
        return stream.filter(isDirectory.negate()).map(LocalStorageFile::get);
    }

    @Override
    public Set<StorageDirectory> dirs(StorageDirectory underDir) {
        return exec(() -> Files
                .walk(underDir.unwrap(Path.class))
                .filter(Files::isDirectory)
                .skip(1)
                .map(LocalStorageDirectory::get)
                .collect(toSet()));
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
