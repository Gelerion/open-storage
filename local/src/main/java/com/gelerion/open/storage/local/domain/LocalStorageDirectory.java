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

//    //TODO:
//    @Override
//    public String currentFolderName() {
//        return currentPath.getFileName().toString();
//    }
//
//    @Override
//    public boolean startsWith(StorageFolder that) {
//        return currentPath.startsWith(that.asString());
//    }
//
    public LocalStorageFile resolve(StorageFile file) {
        Path resolved = doResolve(file);
        return LocalStorageFile.get(resolved);
    }

    public LocalStorageDirectory resolve(StorageDirectory dir) {
        Path resolved = doResolve(dir);
        return LocalStorageDirectory.get(resolved);
    }
//
//    @Override
//    public String toString() {
//        return asString();
//    }
//
    private Path doResolve(StoragePath that) {
        final Path otherPath = that.unwrap(Path.class);
        if (otherPath.startsWith(currentPath)) {
            Path normalizedPath = currentPath.relativize(otherPath);
            return currentPath.resolve(normalizedPath)/*.toAbsolutePath().normalize()*/;
        }

        //currentPath.resolve(currentPath.relativize(otherPath))
        final String left = currentPath.toAbsolutePath().normalize().toString();
        final String right = otherPath.toAbsolutePath().normalize().toString();
        final Path commonPrefix = Paths.get(greatestCommonPrefix(left, right));

        return commonPrefix.resolve(otherPath);

//        return currentPath.resolve(that.toString())/*.toAbsolutePath().normalize()*/;
//        return that.unwrap(Path.class).toAbsolutePath().normalize();
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
