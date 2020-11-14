package com.gelerion.open.storage.s3.reader;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.gelerion.open.storage.api.ops.StorageOperations;
import com.gelerion.open.storage.api.reader.StorageReaderSkeleton;
import com.gelerion.open.storage.s3.model.S3StoragePath;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;

import static com.gelerion.open.storage.api.ops.StorageOperations.exec;

public class S3StorageReader extends StorageReaderSkeleton {
    private final AmazonS3 s3;
    private final S3StoragePath<?> path;

    public S3StorageReader(AmazonS3 s3, S3StoragePath<?> path) {
        this.s3 = s3;
        this.path = path;
    }

//    public static

    @Override
    protected Stream<String> rawContent() {
        return exec(() -> new BufferedReader(new InputStreamReader(getContentStream(), charset))
                .lines());
    }

    @Override
    protected void close() {

    }

    @Override
    public InputStream stream() {
        return getContentStream();
    }

    private InputStream getContentStream() {
        GetObjectRequest request = new GetObjectRequest(path.bucket(), path.key());
        return s3.getObject(request).getObjectContent();
    }

}
