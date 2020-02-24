package com.gelerion.open.storage.local.domain;

import com.gelerion.open.storage.api.domain.StorageDirectory;
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

    //    @Override
//    public LocalStorageDirectory addSubFolder(String folderName) {
//        return folder(currentPath.toString(), folderName);
//    }
//
//    @Override
//    public LocalStorageDirectory addSubFolder(FolderType folderName) {
//        return folder(currentPath.toString(), folderName.toString());
//    }
//
//    @Override
//    public StorageDirectory parentFolder() {
//        return currentPath.getParent() != null ? wrap(currentPath.getParent()) : ROOT;
//    }
//
//    @Override
//    public StorageFile toStorageFile(String fileName) {
//        return LocalStorageFile.locatedAt(currentPath.resolve(fileName));
//    }
//
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
    public LocalStorageFile resolve(LocalStorageFile file) {
        Path resolved = doResolve(file);
        return LocalStorageFile.get(resolved);
    }

    public LocalStorageDirectory resolve(LocalStorageDirectory dir) {
        Path resolved = doResolve(dir);
        return LocalStorageDirectory.get(resolved);
    }
//
//    @Override
//    public String toString() {
//        return asString();
//    }
//
    private Path doResolve(StoragePath otherPath) {
        if (otherPath.unwrap(Path.class).startsWith(currentPath)) {
            Path normalizedPath = currentPath.relativize(otherPath.unwrap(Path.class));
            return currentPath.resolve(normalizedPath).normalize().toAbsolutePath();
        }
        return currentPath.resolve(otherPath.toString()).toAbsolutePath();
    }
}
