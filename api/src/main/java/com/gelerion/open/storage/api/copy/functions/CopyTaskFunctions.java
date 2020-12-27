package com.gelerion.open.storage.api.copy.functions;

import com.gelerion.open.storage.api.domain.StorageFile;

import java.util.function.Function;

public class CopyTaskFunctions {

    //renameTo oldName -> newName
    public static Function<StorageFile, StorageFile> renameTo(String newName) {
        return file -> file.rename(newName);
    }

    public static Function<StorageFile, StorageFile> addPrefix(String prefix) {
        return file -> file.rename(prefix + file.fileName());
    }

    public static Function<StorageFile, StorageFile> addSuffix(String suffix) {
        return file -> file.rename(file.fileName() + suffix);
    }
}
