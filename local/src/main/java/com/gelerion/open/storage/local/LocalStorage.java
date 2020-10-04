package com.gelerion.open.storage.local;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.copy.CopyTask;
import com.gelerion.open.storage.api.copy.flow.CopySource;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.domain.StoragePath;
import com.gelerion.open.storage.api.exceptions.StorageOperationException;
import com.gelerion.open.storage.api.ops.ListFilesOption;
import com.gelerion.open.storage.api.reader.StorageReader;
import com.gelerion.open.storage.api.rename.DirectoryRenamer;
import com.gelerion.open.storage.api.rename.FileRenamer;
import com.gelerion.open.storage.api.rename.Renamer;
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
import static com.gelerion.open.storage.local.dsl.PathTypeDsl.checkLocalOrFail;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Collections.reverseOrder;
import static java.util.stream.Collectors.toSet;

public class LocalStorage implements Storage {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static Storage newLocalStorage() {
        return new LocalStorage();
    }

    @Override
    public String scheme() {
        return "local";
    }

    @Override
    public <T extends StoragePath<T>> Storage create(T path) {
        checkLocalOrFail(path)
                .ifFile(file -> exec(()-> createFileInternal(file)))
                .ifDir(dir   -> exec(() -> createDirInternal(dir)));
        return this;
    }

    @Override
    public <T extends StoragePath<T>> void delete(T path) {
        checkLocalOrFail(path)
                .ifFile(this::deleteFile)
                .ifDir(this::deleteDir);
    }

    @Override
    public <T extends StoragePath<T>> long size(T path) {
        return checkLocalOrFail(path)
                .whenFile(this::size)
                .whenDir(this::size);
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
    public boolean exists(StoragePath<?> path) {
        return Files.exists(unwrapped(path));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends StoragePath<T>> Renamer<T> rename(T source) {
        return checkLocalOrFail(source)
                .whenFile(file -> (Renamer<T>) rename(file))
                .whenDir(dir   -> (Renamer<T>) rename(dir));
    }


    public Renamer<StorageDirectory> rename(StorageDirectory source) {
        return new DirectoryRenamer(source, this);
    }

    public Renamer<StorageFile> rename(StorageFile source) {
        return new FileRenamer(source, this);
    }

    //TODO; check invariants source- target
    @Override
    @SuppressWarnings("unchecked")
    public <T extends StoragePath<T>> T move(T source, T target) {
        return checkLocalOrFail(source)
                .whenFile(file -> (T) move(file, (StorageFile) target))
                .whenDir(dir   -> (T) move(dir, (StorageDirectory) target));
    }

    public StorageDirectory move(StorageDirectory source, StorageDirectory target) {
        return exec(() ->
                LocalStorageDirectory.get(Files.move(
                        unwrapped(source),
                        unwrapped(source.resolve(target)), REPLACE_EXISTING)
                )
        );
    }

    LocalStorageFile move(StorageFile source, StorageFile target) {
        return exec(() -> {
            final StorageFile resolved = source.resolve(target);
            createDirInternal(resolved.parentDir());
            return LocalStorageFile.get(Files.move(
                    unwrapped(source),
                    unwrapped(resolved), REPLACE_EXISTING));
            });
    }

    @Override
    public CopySource copy() {
        return CopyTask.newCopyTask(this);
    }

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
    @SuppressWarnings("unchecked")
    public <T extends StoragePath<T>> T absolutePath(T path) {
        if (path instanceof LocalStorageFile) {
            return (T) LocalStorageFile.get(path.unwrap(Path.class).toAbsolutePath());
        }

        if (path instanceof LocalStorageDirectory) {
            return (T) LocalStorageDirectory.get(path.unwrap(Path.class).toAbsolutePath());
        }

        throw new StorageOperationException("path must be either LocalStorageDirectory or LocalStorageFile");
    }

    private Path unwrapped(StoragePath<?> file) {
        return file.unwrap(Path.class);
    }

    private void delete(Path path) {
        run(() -> Files.deleteIfExists(path));
    }

    private Storage createFileInternal(StorageFile file) throws IOException {
        createDirInternal(file.parentDir());
        Files.createFile(file.unwrap(Path.class));
        return this;
    }

    private Storage createDirInternal(StorageDirectory directory) throws IOException {
        Path path = directory.unwrap(Path.class);
        if (!Files.exists(path)) Files.createDirectories(path);
        return this;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void deleteDir(StorageDirectory directory) {
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

    private void deleteFile(StorageFile file) {
        delete(unwrapped(file));
    }

    private long size(StorageDirectory dir) {
        return exec(() -> {
            Predicate<Path> isDirectory = Files::isDirectory;
            return Files.walk(unwrapped(dir))
                    .filter(isDirectory.negate())
                    .mapToLong(path -> execOpt(() -> Files.size(path)).orElse(0))
                    .sum();
        });
    }

    private long size(StorageFile file) {
        return execOpt(() -> Files.size(unwrapped(file))).orElse(0);
    }
}
