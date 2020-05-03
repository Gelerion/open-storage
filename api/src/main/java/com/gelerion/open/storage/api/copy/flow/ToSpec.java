package com.gelerion.open.storage.api.copy.flow;

import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;

import java.util.Objects;

public class ToSpec {
    protected Storage targetStorage;
    private StorageDirectory tgtDir;

    public ToSpec(StorageDirectory dir) {
        this.tgtDir = dir;
    }

    public static ToSpec dir(StorageDirectory dir) {
        Objects.requireNonNull(dir);
        return new ToSpec(dir);
    }

    ToSpec withStorage(Storage storage) {
        this.targetStorage = storage;
        return this;
    }

    //TODO: recursively resolving
    /*
    protected void doCopyImpl(FileSystem srcFs, FileStatus srcStatus, FileSystem dstFs, Path dstPath, boolean isFirstCopy) throws Exception {
        Path fromPath = srcStatus.getPath();
        //dstPath = checkDest(fromPath.getName(), dstFs, dstPath, copyOptions.overwrite());

        //if already resolved with same name, do not add unnecessary inner path
        if(!isFirstCopy)
            dstPath = Objects.equals(dstPath.getName(), fromPath.getName()) ? dstPath : new Path(dstPath, fromPath.getName());
        else if(copyContext.destination().isDirectory())
            dstPath = new Path(dstPath, fromPath.getName()); //if dest is file, no need to resolve

        if (srcStatus.isDirectory()) { //get all subdirectories
            dstPath = checkValid(srcFs, dstFs, dstPath, isFirstCopy, fromPath);
            FileStatus contents[] = getPathContent(srcFs, fromPath);

            for (FileStatus content : contents) {
                doCopyImpl(srcFs, content, dstFs, new Path(dstPath, content.getPath().getName()), false );
            }
        }
        else {
            copySingleFile(srcFs, srcStatus, dstFs, dstPath);
        }
    }
     */
    StorageFile resolve(StorageFile sourceFile) {
        return tgtDir.toStorageFile(sourceFile.fileName());
    }

    StorageDirectory dir() {
        return tgtDir;
    }
}
