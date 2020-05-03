package com.gelerion.open.storage.api.copy.source;

import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

public interface CopySource {
    //Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    Stream<StorageFile> files();

    static CopySource glob(String pattern) {
        //TODO: check not blank
        //return new Glob(pattern);
        return null;
    }

    static CopySource file(StorageFile file) {
        return () -> Stream.of(file);
    }

    static CopySource dir(StorageDirectory dir) {
        //storage.files(dir)
        return null;

    }
}


