package com.gelerion.open.storage.s3.writer;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.gelerion.open.storage.api.exceptions.StorageOperationException;
import com.gelerion.open.storage.api.ops.StorageOperations.VoidExceptional;
import com.gelerion.open.storage.api.writer.StorageWriter;
import com.gelerion.open.storage.s3.exceptions.recoverable.S3StorageRecoverableException;
import com.gelerion.open.storage.s3.exceptions.unrecoverable.S3StorageBucketDoesNotExistException;
import com.gelerion.open.storage.s3.invoker.Invoker;
import com.gelerion.open.storage.s3.model.S3StorageFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

public class S3StorageWriter implements StorageWriter/*, StorageUploader*/ {
    private static final String LINE_SEP = System.getProperty("line.separator");

    private final S3StorageFile file;
    private final AmazonS3 s3;
    private final Invoker invoker;

    private S3StorageWriter(S3StorageFile file, AmazonS3 s3, Invoker invoker) {
        this.file = file;
        this.s3 = s3;
        this.invoker = invoker;
    }

    public static S3StorageWriter output(S3StorageFile file, AmazonS3 s3) {
        return new S3StorageWriter(file, s3, Invoker.getDefault());
    }

    @Override
    public void write(Stream<String> content) {
        write(content.collect(toList()));
    }

    @Override
    public void write(String content) {
        doWrite(() -> s3.putObject(file.bucket(), file.key(), content));
    }

    @Override
    public void write(Collection<String> content) {
        doWrite(() -> {
            StringBuilder out = new StringBuilder(content.size());
            for (String line : content) {
                out.append(line);
                if (!line.endsWith(LINE_SEP)) out.append(LINE_SEP);
            }

            byte[] utf8Content = out.toString().getBytes(UTF_8); //ensure UTF-8
            InputStream is = new ByteArrayInputStream(utf8Content);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("text/plain"); //TODO add support for other mime-types, like: text/csv and application/json
            metadata.setContentLength(utf8Content.length);

            s3.putObject(file.bucket(), file.key(), is, metadata);
        });
    }

    @Override
    public void write(byte[] out) {
        doWrite(() -> {
            InputStream is = new ByteArrayInputStream(out);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("text/plain"); //TODO add support for other mime-types
            metadata.setContentLength(out.length);

            s3.putObject(file.bucket(), file.key(), is, metadata);
        });

    }

    private void doWrite(VoidExceptional body) {
        invoker.customize()
                .retryOn(S3StorageRecoverableException.class)
                .when(bucketIsMissing()).then(() -> s3.createBucket(file.bucket()))
                .run(body::execute);
    }

/*    @Override
    public void upload(File file) {
        String s3Key = storage.mountedAt().resolve(this.file).asString();
        storage.deleteObject(s3Key);
        storage.putObject(s3Key, file);
    }*/

/*    @Override
    public void upload(URL url) {
        String s3Key = storage.mountedAt().resolve(this.file).asString();
        storage.deleteObject(s3Key);
        //TODO: cleaner & faster way. this one hurts performance
        voidStorageOperation(() -> {
            InputStream is = url.openStream();
            storage.putObject(s3Key, is, new ObjectMetadata());
        });
    }*/

    @Override
    public void append(Stream<String> content) {
        throw new StorageOperationException("S3 append not supported");
    }

    @Override
    public void append(Collection<String> content) {
        throw new StorageOperationException("S3 append not supported");
    }

    private Predicate<Throwable> bucketIsMissing() {
        return ex -> ex instanceof S3StorageBucketDoesNotExistException;
    }
}

