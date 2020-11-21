package com.gelerion.open.storage.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.copy.flow.CopySource;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.domain.StoragePath;
import com.gelerion.open.storage.api.dsl.PathImplCheckerDsl;
import com.gelerion.open.storage.api.ops.ListFilesOption;
import com.gelerion.open.storage.api.reader.StorageReader;
import com.gelerion.open.storage.api.rename.Renamer;
import com.gelerion.open.storage.api.writer.StorageWriter;
import com.gelerion.open.storage.s3.exceptions.recoverable.S3StorageRecoverableException;
import com.gelerion.open.storage.s3.exceptions.unrecoverable.S3StorageBucketDoesNotExistException;
import com.gelerion.open.storage.s3.invoker.Invoker;
import com.gelerion.open.storage.s3.invoker.InvokerConfig;
import com.gelerion.open.storage.s3.model.S3StorageDirectory;
import com.gelerion.open.storage.s3.model.S3StorageFile;
import com.gelerion.open.storage.s3.model.S3StoragePath;
import com.gelerion.open.storage.s3.provider.AwsClientsProvider;
import com.gelerion.open.storage.s3.reader.S3StorageReader;
import com.gelerion.open.storage.s3.writer.S3StorageWriter;
import net.jodah.failsafe.function.CheckedRunnable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

//TODO: global -- assert path noy null
public class S3Storage implements Storage {
    private final AmazonS3 s3;
    private final Invoker invoker;
    private final PathImplCheckerDsl<S3StorageFile, S3StorageDirectory> dsl;

    public static S3Storage newS3Storage() {
        return newS3Storage(AwsClientsProvider.getDefault(), InvokerConfig.getDefault());
    }

    public static S3Storage newS3Storage(AwsClientsProvider clientsProvider) {
        return new S3Storage(clientsProvider.s3Client(), InvokerConfig.getDefault());
    }

    public static S3Storage newS3Storage(AwsClientsProvider clientsProvider, InvokerConfig config) {
        return new S3Storage(clientsProvider.s3Client(), config);
    }

    public S3Storage(AmazonS3 s3, InvokerConfig config) {
        this.s3 = s3;
        this.dsl = PathImplCheckerDsl.create(S3StorageFile.class, S3StorageDirectory.class);
        this.invoker = new Invoker(config);
    }

    @Override
    public String scheme() {
        return "s3";
    }

    @Override
    public <T extends StoragePath<T>> Storage create(T path) {
        return invoker
                .customize()
                .retryOn(S3StorageRecoverableException.class)
                .when(bucketIsMissing()).then(createBucket(path))
                .exec(() -> {
                    S3StoragePath<?> s3Path = (S3StoragePath<?>) path;

                    // create meta-data for your folder and set content-length to 0
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentLength(0);

                    // create empty content
                    InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

                    s3.putObject(s3Path.bucket(), s3Path.key(), emptyContent, metadata);
                    return this;
                });
    }

    @Override
    public <T extends StoragePath<T>> void delete(T path) {
        dsl.checkValidImplOrFail(path)
                .ifFile(this::delete)
                .ifDir(this::delete);
    }

    @Override
    public <T extends StoragePath<T>> long size(T path) {
        return 0;
    }

    @Override
    public StorageReader reader(StorageFile file) {
        return new S3StorageReader(s3, (S3StoragePath<?>) file);
    }

    //TODO: refactor cast
    @Override
    public StorageWriter writer(StorageFile file) {
        return S3StorageWriter.output((S3StorageFile) file, s3);
    }

    @Override
    public boolean exists(StoragePath<?> path) {
        return false;
    }

    @Override
    public <T extends StoragePath<T>> Renamer<T> rename(T source) {
        return null;
    }

    @Override
    public <T extends StoragePath<T>> T move(T source, T target) {
        return null;
    }

    @Override
    public CopySource copy() {
        return null;
    }

    @Override
    public Set<StorageFile> files(StorageDirectory underDir, ListFilesOption... opts) {
        return null;
    }

    @Override
    public Set<StorageDirectory> dirs(StorageDirectory underDir) {
        return s3.listBuckets()
                .stream()
                .map(bucket -> S3StorageDirectory.get(bucket.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    public <T extends StoragePath<T>> T absolutePath(T path) {
        return null;
    }

    public void createBucket(String bucket) {
        s3.createBucket(bucket);
    }

    private void delete(S3StorageFile s3file) {
        invoker.run(() -> s3.deleteObject(s3file.bucket(), s3file.key()));
    }

    //TODO: test delete fails in the middle
    private void delete(S3StorageDirectory dir) {
        invoker.run(() -> {
            ListObjectsV2Request request = listObjectsReq(dir);
            ListObjectsV2Result result;

            do {
                result = s3.listObjectsV2(request);
                deleteObjects(dir, result);
                String token = result.getNextContinuationToken();
                request.setContinuationToken(token);
            } while (result.isTruncated());
        });
    }

    private ListObjectsV2Request listObjectsReq(S3StorageDirectory dir) {
        return new ListObjectsV2Request()
                .withBucketName(dir.bucket())
                .withPrefix(dir.key())
                .withMaxKeys(1000);
    }

    private void deleteObjects(S3StorageDirectory dir, ListObjectsV2Result objects) {
        List<KeyVersion> keys = objects.getObjectSummaries()
                .stream()
                .map(S3ObjectSummary::getKey)
                .map(KeyVersion::new)
                .collect(toList());

        DeleteObjectsRequest req = new DeleteObjectsRequest(dir.bucket()).withKeys(keys);
        s3.deleteObjects(req);
    }

    private <T extends StoragePath<T>> CheckedRunnable createBucket(T path) {
        return () -> createBucket(((S3StoragePath<?>) path).bucket());
    }

    private Predicate<Throwable> bucketIsMissing() {
        return ex -> ex instanceof S3StorageBucketDoesNotExistException;
    }
}
