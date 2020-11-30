package com.gelerion.open.storage.s3.model;

import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.exceptions.StorageOperationException;
import com.gelerion.open.storage.s3.utils.S3KeySplitter;

import java.nio.file.Path;
import java.nio.file.Paths;

public class S3StorageDirectory extends S3StoragePath<StorageDirectory> implements StorageDirectory {

    protected S3StorageDirectory(String dir) {
        super(dir);
    }

    @Override
    public S3StorageFile resolve(S3StorageFile file) {
        //TODO; extract schema from workingPath
        return S3StorageFile.get("s3a://" + bucket + "/" + doResolve(file));
    }

    @Override
    public S3StorageDirectory resolve(S3StorageDirectory dir) {
        //TODO; extract schema from workingPath
        return S3StorageDirectory.get("s3a://" + bucket + "/" + doResolve(dir));
    }

    public S3StorageFile resolve(String key) {
        S3StorageFile that = S3StorageFile.get("s3a://" + bucket + "/" + key);
        return this.resolve(that);
    }


//    public static S3StorageDirectory get(String dir, String... subDirs) {
//        return new S3StorageDirectory(dir);
//    }

    public static S3StorageDirectory get(String dir) {
        return new S3StorageDirectory(dir);
    }

    @Override
    public StorageFile toStorageFile(String fileName) {
        //TODO: check maybe
        return S3StorageFile.get(workingPath + "/" + fileName.replaceAll("/", ""));
    }

    @Override
    public StorageDirectory addSubDirectory(String dir) {
        //TODO: check
        return S3StorageDirectory.get(workingPath + "/" + dir.replaceAll("/", ""));
    }

    @Override
    public String dirName() {
        return !key.contains("/") ? key : key.substring(key.lastIndexOf("/") + 1);
    }

    private String doResolve(S3StoragePath<?> that) {
        if (!this.bucket.equals(that.bucket)) {
            throw new StorageOperationException("Can't resolve files in different buckets, this bucket - "
                    + this.bucket + ", that bucket - " + that.bucket);
        }

        //quite a hack :)
        Path thisPath = Paths.get(this.key);
        Path thatPath = Paths.get(that.key);

//        if (thatPath.startsWith(thisPath)) {
//            return workingPath.resolveSibling(thatPath);
//        }

        Path normalizedPath = thisPath.relativize(thatPath);
        return thisPath.resolve(normalizedPath).normalize().toString().replaceAll("\\\\", "/")/*.toAbsolutePath().normalize()*/;

    }

    @Override
    public StorageDirectory butLast() {
        return S3StorageDirectory.get("s3a://" + bucket + "/" + S3KeySplitter.split(key).butLast().key());
    }
}
