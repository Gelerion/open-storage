package com.gelerion.open.storage.api;

import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StoragePath;

import java.net.URL;
import java.util.Set;
import java.util.stream.Stream;

public interface Storage {
    Storage create(StorageDirectory folder);

    void delete(StorageDirectory folder);
//
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

//    StorageReader reader(StorageFile file);

//    StorageWriter writer(StorageFile file);

//    StorageUploader uploader(StorageFile file);

    boolean exists(StoragePath path);

    void rename(StorageDirectory source, StorageDirectory target);

    void rename(StorageFile source, StorageFile target);

    void copy(StoragePath source, StoragePath target);

//    SourcePath copy();

    //TODO: predicate pushdown
    /**
     * set of files under specific folder, nested folders aren't taken into account
     */
    Set<StorageFile> files(StorageDirectory underFolder);

    //<R> R files(Function<Stream<StorageFile>, R> func);

    Stream<StorageDirectory> subFolders(StorageDirectory base);

    StorageDirectory fullPath(StorageDirectory folder);

    StorageFile fullPath(StorageFile file);

    /**
     * @param file Path to the file
     * @param partnerId The name of the federated user. The name is used as an identifier for the temporary security credentials
     * @throws MandatoryFileNotFoundException If file does not exist
     */
    URL downloadUrl(StorageFile file, String partnerId);
}
