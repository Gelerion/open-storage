package com.gelerion.open.storage.local.domain;

import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.domain.StoragePath;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalStorageDirectory extends LocalStoragePath implements StorageDirectory {

    protected LocalStorageDirectory(Path dir) {
        super(dir);
    }

    public static LocalStorageDirectory get(String dir, String... subDirs) {
        return new LocalStorageDirectory(Paths.get(dir, subDirs));
    }

    public static LocalStorageDirectory get(String dir) {
        return new LocalStorageDirectory(Paths.get(dir));
    }

    public static LocalStorageDirectory get(Path dir) {
        return new LocalStorageDirectory(dir);
    }

    @Override
    public LocalStorageFile toStorageFile(String fileName) {
        return LocalStorageFile.get(currentPath.resolve(fileName));
    }

    @Override
    public LocalStorageDirectory addSubDirectory(String dir) {
        return get(currentPath.toString(), dir);
    }

    @Override
    public String dirName() {
        return currentPath.getFileName().toString();
    }

    public LocalStorageFile resolve(StorageFile file) {
        Path resolved = doResolve(file);
        return LocalStorageFile.get(resolved);
    }

    public LocalStorageDirectory resolve(StorageDirectory dir) {
        Path resolved = doResolve(dir);
        return LocalStorageDirectory.get(resolved);
    }

    @Override
    public StorageDirectory butLast() {
        return currentPath.getNameCount() == 1 ? this :
                LocalStorageDirectory.get(currentPath.subpath(1, currentPath.getNameCount()));
    }

    private Path doResolve(StoragePath that) {
        final Path thatPath = currentPath.isAbsolute() ? that.unwrap(Path.class).toAbsolutePath() : that.unwrap(Path.class);
        final Path thisPath = thatPath.isAbsolute() ? currentPath.toAbsolutePath() : currentPath;

        if (thatPath.startsWith(thisPath)) {
            return currentPath.resolveSibling(thatPath);
        }

        Path normalizedPath = thisPath.relativize(thatPath);
        return thisPath.resolve(normalizedPath)/*.toAbsolutePath().normalize()*/;

    }

    public String greatestCommonPrefix(String a, String b) { //invariant - roots are always equals
        int minLength = Math.min(a.length(), b.length());
        for (int i = 0; i < minLength; i++) {
            if (a.charAt(i) != b.charAt(i)) {
                return a.substring(0, i);
            }
        }
        return a.substring(0, minLength);
    }
}
