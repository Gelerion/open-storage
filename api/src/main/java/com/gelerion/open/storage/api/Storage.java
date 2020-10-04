package com.gelerion.open.storage.api;

import com.gelerion.open.storage.api.copy.flow.CopySource;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.domain.StoragePath;
import com.gelerion.open.storage.api.ops.ListFilesOption;
import com.gelerion.open.storage.api.reader.StorageReader;
import com.gelerion.open.storage.api.rename.Renamer;
import com.gelerion.open.storage.api.writer.StorageWriter;

import java.util.Set;

public interface Storage {
    String scheme();

    /**
     * Create empty file or empty directory
     */
    <T extends StoragePath<T>> Storage create(T path);

    <T extends StoragePath<T>> void delete(T path);

    /**
     * @return size in bytes
     */
    <T extends StoragePath<T>> long size(T path);


//    StorageFileMetadata metadata(StorageFile file);

    StorageReader reader(StorageFile file);

    StorageWriter writer(StorageFile file);

//    StorageUploader uploader(StorageFile file);

    boolean exists(StoragePath<?> path);

    <T extends StoragePath<T>> Renamer<T> rename(T source);

    <T extends StoragePath<T>> T move(T source, T target);

    CopySource copy();

//    Stream<StoragePath> glob(String pattern)

    //TODO: predicate pushdown
    /**
     * set of files under specific directory, nested dirs aren't taken into account
     */
    Set<StorageFile> files(StorageDirectory underDir, ListFilesOption... opts);
    //<R> R files(Function<Stream<StorageFile>, R> func);

    Set<StorageDirectory> dirs(StorageDirectory underDir);

    <T extends StoragePath<T>> T absolutePath(T path);
}
