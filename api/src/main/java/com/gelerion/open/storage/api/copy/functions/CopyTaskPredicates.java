package com.gelerion.open.storage.api.copy.functions;

import com.gelerion.open.storage.api.domain.StorageFile;

import java.util.function.Predicate;

public class CopyTaskPredicates {

    public static Predicate<StorageFile> pathContains(String part) {
        return file -> file.contains(part);
    }

    public static Predicate<StorageFile> fileNameEndsWith(String suffix) {
        return file -> file.fileName().endsWith(suffix);
    }

    public static Predicate<StorageFile> fileNameStartsWith(String prefix) {
        return file -> file.fileName().startsWith(prefix);
    }


}
