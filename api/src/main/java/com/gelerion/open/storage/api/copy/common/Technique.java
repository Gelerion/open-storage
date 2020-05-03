package com.gelerion.open.storage.api.copy.common;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.domain.StoragePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class Technique {
    protected static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final OnMatchedFileListener LOGGING_LISTENER = (fs, sourcePath) -> log.info("Copy from: " + sourcePath);


//    public static Technique file(StorageFile file) {
//
//    }
//
//    public static Technique dir(StorageDirectory file) {
//
//    }
//
//    public static Technique path(StoragePath path) {
//
//    }

    @FunctionalInterface
    public interface OnMatchedFileListener {
        void onMatchedFile(Storage storage, StoragePath path);
    }
}
