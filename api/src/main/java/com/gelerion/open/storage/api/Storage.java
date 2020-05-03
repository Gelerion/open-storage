package com.gelerion.open.storage.api;

import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.domain.StoragePath;
import com.gelerion.open.storage.api.reader.StorageReader;
import com.gelerion.open.storage.api.writer.StorageWriter;

import java.util.Set;

public interface Storage {
    String name();

    Storage create(StorageDirectory folder);

    void delete(StorageDirectory folder);

    void delete(StorageFile file);

    default void delete(StoragePath path) {
        if (path instanceof StorageFile) {
            delete((StorageFile) path);
        }
        if (path instanceof StorageDirectory) {
            delete((StorageDirectory) path);
        }
    }

    /**
     * @return size in bytes
     */
    long size(StorageDirectory folder);

    long size(StorageFile file);

//    StorageFileMetadata metadata(StorageFile file);

    StorageReader reader(StorageFile file);

    StorageWriter writer(StorageFile file);

//    StorageUploader uploader(StorageFile file);

    boolean exists(StoragePath path);

    //retrun renamed file
    StorageFile rename(StorageDirectory source, StorageDirectory target);

    StorageFile rename(StorageFile source, StorageFile target);

    void copy(StoragePath source, StoragePath target);

//    SourcePath copy();

//    Stream<StoragePath> glob(StoragePath path)

    //TODO: predicate pushdown
    /**
     * set of files under specific directory, nested dirs aren't taken into account
     */
    Set<StorageFile> files(StorageDirectory underDir);
    //<R> R files(Function<Stream<StorageFile>, R> func);

    Set<StorageDirectory> dirs(StorageDirectory underDir);

    StorageDirectory fullPath(StorageDirectory folder);

    StorageFile fullPath(StorageFile file);

    //TODO:
    //glob()
}
