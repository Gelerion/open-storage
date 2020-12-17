package com.gelerion.open.storage.s3.test;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.s3.S3Storage;
import com.gelerion.open.storage.s3.model.S3StorageDirectory;
import com.gelerion.open.storage.s3.model.S3StorageFile;
import com.gelerion.open.storage.s3.provider.AwsClientsProvider;
import com.gelerion.open.storage.s3.provider.AwsConfig;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.stream.Stream;

import static com.gelerion.open.storage.api.copy.flow.TargetSpec.dir;
import static com.gelerion.open.storage.api.copy.options.StandardStorageCopyOption.DELETE_SOURCE_FILES;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
public class S3StorageCopyTaskTest {
    private static final String ACCESS_KEY = "admin";
    private static final String SECRET_KEY = "12345678";
//    private static final int PORT = 9000;

    private static final Network NETWORK = Network.newNetwork();

    @Container
    public static final GenericContainer<?> MINIO = new GenericContainer<>(DockerImageName.parse("minio/minio"))
            .withEnv("MINIO_ACCESS_KEY", ACCESS_KEY)
            .withEnv("MINIO_SECRET_KEY", SECRET_KEY)
            //-p 9000:9000
//            .withCreateContainerCmdModifier(exposeCustomPort())
            .withNetwork(NETWORK)
            .withCommand("server /data")
            .waitingFor(serviceIsLive());

    private final String testBucket = "test-bucket";
    private String minioServerEndpoint;

    private S3Storage storage;

    @BeforeAll
    protected void init() {
        this.minioServerEndpoint = format("http://%s:%s", MINIO.getContainerIpAddress(), MINIO.getMappedPort(9000));
        this.storage = S3Storage.newS3Storage(new AwsClientsProvider(initAwsConfig()));
        System.out.println("MINIO.getMappedPort(9000) = " + MINIO.getMappedPort(9000));
    }

    @Test
    void copyFile() {
        String dir = "s3://" + testBucket + "/" + "abc";
        String fileName = "test.txt";
        StorageFile file = S3StorageDirectory.get(dir).toStorageFile(fileName);
        storage.writer(file).write(Stream.of("Hello world!", "What a perfect day!"));

        StorageDirectory tgtDir = S3StorageDirectory.get("s3://" + testBucket + "/abc/efg");

        //source: abc/text.txt
        //target: abc/efg
        //result: abc/efg/test.txt
        storage.copy().source(file).target(tgtDir).execute();

        assertTrue(storage.exists(tgtDir.toStorageFile(fileName)));
    }

    @Test
    void copyFileToADifferentDirectory() {
        String dir = "s3://" + testBucket + "/" + "abc";
        String fileName = "test.txt";
        StorageFile file = S3StorageDirectory.get(dir).toStorageFile(fileName);
        storage.writer(file).write(Stream.of("Hello world!", "What a perfect day!"));

        StorageDirectory tgtDir = S3StorageDirectory.get("s3://" + testBucket + "/efg");

        //source: abc/text.txt
        //target: efg
        //result: efg/test.txt
        storage.copy().source(file).target(tgtDir).execute();

        assertTrue(storage.exists(tgtDir.toStorageFile(fileName)));
    }

    @Test
    void copyFileToADifferentDirectoryAndRename() {
        String dir = "s3://" + testBucket + "/" + "abc";
        String fileName = "test.txt";
        StorageFile file = S3StorageDirectory.get(dir).toStorageFile(fileName);
        storage.writer(file).write(Stream.of("Hello world!", "What a perfect day!"));

        StorageDirectory tgtDir = S3StorageDirectory.get("s3://" + testBucket + "/efg");

        //source: abc/text.txt
        //target: efg
        //result: efg/test.txt
        storage.copy()
                .source(file)
                .target(dir(tgtDir).map(it -> it.rename("test2.txt")))
                .execute();

        assertTrue(storage.exists(tgtDir.toStorageFile("test2.txt")));
    }

//    @Test
//    void copyAndRenameWithinSameDir() {
//        String dir = "abc";
//        String fileName = "test.txt";
//        StorageFile file = createDir(dir).toStorageFile(fileName);
//        storage.writer(file).write(Stream.of("Hello world!", "What a perfect day!"));
//
//        StorageDirectory tgtDir = createDir("abc");
//
//        //source: abc/text.txt
//        //target: abc/
//        //result: abc/test2.txt
//        String newName = "test2.txt";
//        storage.copy()
//                .source(file)
//                .target(dir(tgtDir).map(it -> it.rename(newName)))
//                .execute();
//
//        assertTrue(Files.exists(tgtDir.toStorageFile(newName).unwrap(Path.class)));
//    }

    @Test
    void copyFileAndDeleteSource() {
        String dir = "s3://" + testBucket + "/" + "abc";
        String fileName = "test.txt";
        StorageFile file = S3StorageDirectory.get(dir).toStorageFile(fileName);
        storage.writer(file).write(Stream.of("Hello world!", "What a perfect day!"));

        StorageDirectory tgtDir = S3StorageDirectory.get("s3://" + testBucket + "/" + "cbs");

        //source: abc/text.txt
        //target: cbs/

        //expected result:
        // source: abc/
        // target: cbs/test.txt
        storage.copy().source(file).target(tgtDir).options(DELETE_SOURCE_FILES).execute();

        assertFalse(storage.exists(file));
        assertTrue(storage.exists(tgtDir.toStorageFile(fileName)));
    }

    public StorageFile pathToStorageFile(String file) {
        return S3StorageFile.get("s3://" + testBucket + "/" + file);
    }

    public StorageDirectory pathToStorageDir(String dir) {
        return S3StorageDirectory.get("s3://" + testBucket + "/" + dir);
    }


    private MinioClient initMinioClient() {
        return MinioClient.builder()
                .credentials(ACCESS_KEY, SECRET_KEY)
                .endpoint(minioServerEndpoint)
                .build();
    }

    private AwsConfig initAwsConfig() {
        ClientConfiguration clientConf = new ClientConfiguration()
                .withSignerOverride("AWSS3V4SignerType");

        AwsClientBuilder.EndpointConfiguration endpointConf = new AwsClientBuilder.EndpointConfiguration(minioServerEndpoint, Regions.US_EAST_1.name());

        return new AwsConfig()
                .withEndpointConfig(endpointConf)
                .withClientConfig(clientConf)
                .withCredentials(ACCESS_KEY, SECRET_KEY)
                .enablePathStyleAccess();
    }

    private static WaitStrategy serviceIsLive() {
        return new HttpWaitStrategy()
                .forPath("/minio/health/live")
                .forPort(9000)
                .withStartupTimeout(Duration.ofSeconds(10));
    }
}
