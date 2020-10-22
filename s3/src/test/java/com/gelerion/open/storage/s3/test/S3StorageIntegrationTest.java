package com.gelerion.open.storage.s3.test;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.api.ops.StorageOperations.Exceptional;
import com.gelerion.open.storage.api.ops.StorageOperations.VoidExceptional;
import com.gelerion.open.storage.s3.S3Storage;
import com.gelerion.open.storage.s3.domain.S3StorageDirectory;
import com.gelerion.open.storage.s3.domain.S3StorageFile;
import com.gelerion.open.storage.s3.provider.AwsClientsProvider;
import com.gelerion.open.storage.s3.provider.AwsConfig;
import com.gelerion.open.storage.test.StorageIntegrationTest;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import io.minio.*;
import io.minio.messages.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class S3StorageIntegrationTest extends StorageIntegrationTest {
    private static final String ACCESS_KEY = "admin";
    private static final String SECRET_KEY = "12345678";
    private static final int PORT = 9000;

    private static final Network NETWORK = Network.newNetwork();

    @Container
    public static final GenericContainer<?> MINIO = new GenericContainer<>(DockerImageName.parse("minio/minio"))
            .withEnv("MINIO_ACCESS_KEY", ACCESS_KEY)
            .withEnv("MINIO_SECRET_KEY", SECRET_KEY)
            //-p 9000:9000
            .withCreateContainerCmdModifier(exposeCustomPort())
            .withNetwork(NETWORK)
            .withCommand("server /data")
            .waitingFor(serviceIsLive());

    private final String testBucket = "test-bucket";
    private final String minioServerEndpoint = format("http://%s:9000", MINIO.getContainerIpAddress());

    private McOps mc;
    private S3Storage s3Storage;

    @BeforeAll
    protected void init() {
        this.mc = new McOps(initMinioClient(), testBucket);
        this.s3Storage = S3Storage.newS3Storage(new AwsClientsProvider(initAwsConfig()));
        super.init();
    }

    @AfterEach
    protected void afterEach() throws IOException {
        super.afterEach();
        mc.removeBucket();
    }

    @Override
    public void assertFileExist(StorageFile file) throws IOException {
        S3StorageFile s3File = (S3StorageFile) file;
        ObjectStat stats = mc.stats(s3File.key());
        assertEquals(file.name(), stats.name());
    }

    @Override
    public void assertFileSizeEqualsTo(StorageFile file, long expectedSize) throws IOException {
        S3StorageFile s3File = (S3StorageFile) file;

        long actualSize = mc.listObjects(s3File.key())
                .filter(item -> item.objectName().equals(file.fileName()))
                .findFirst()
                .map(Item::size)
                .orElseThrow(() -> new RuntimeException("File not found " + file));

        assertEquals(expectedSize, actualSize);
    }

    @Override
    public void assertFileHasContent(Collection<String> lines) throws IOException {
        throw new RuntimeException();
    }

    @Override
    public void assertDirExist(StorageDirectory dir) throws IOException {
        throw new RuntimeException();
    }

    // ----------------------------------------------------------------------------------
    // -------------- Test Helpers
    // ----------------------------------------------------------------------------------

    @Override
    public Storage storageImpl() {
        return s3Storage;
    }

    @Override
    public StorageFile pathToStorageFile(String file) {
        return S3StorageFile.get("s3://" + testBucket + "/" + file);
    }

    @Override
    public StorageDirectory pathToStorageDir(String dir) {
        throw new RuntimeException();
    }

    @Override
    protected void deleteFile(StorageFile file) throws IOException {
        S3StorageFile s3File = (S3StorageFile) file;
        mc.removeObject(s3File.key());
    }

    @Override
    protected void deleteDir(StorageDirectory dir) throws IOException {
        S3StorageDirectory s3Dir = (S3StorageDirectory) dir;
        mc.listObjects(s3Dir.key()).map(Item::objectName).forEach(mc::removeObject);
    }

    // ----------------------------------------------------------------------------------
    // -------------- Internal
    // ----------------------------------------------------------------------------------

    private MinioClient initMinioClient() {
        return MinioClient.builder()
                .credentials(ACCESS_KEY, SECRET_KEY)
                .endpoint(minioServerEndpoint)
                .build();
    }

    private AwsConfig initAwsConfig() {
        ClientConfiguration clientConf = new ClientConfiguration()
                .withSignerOverride("AWSS3V4SignerType");

        EndpointConfiguration endpointConf = new EndpointConfiguration(minioServerEndpoint, Regions.US_EAST_1.name());

        return new AwsConfig()
                .withEndpointConfig(endpointConf)
                .withClientConfig(clientConf)
                .withCredentials(ACCESS_KEY, SECRET_KEY)
                .enablePathStyleAccess();
    }

    private static WaitStrategy serviceIsLive() {
        return new HttpWaitStrategy()
                .forPath("/minio/health/live")
                .forPort(PORT)
                .withStartupTimeout(Duration.ofSeconds(10));
    }

    private static Consumer<CreateContainerCmd> exposeCustomPort() {
        return cmd -> {
            HostConfig hostConfig = cmd.getHostConfig();
            Objects.requireNonNull(hostConfig);
            hostConfig.withPortBindings(new PortBinding(Ports.Binding.bindPort(PORT), new ExposedPort(PORT)));
        };
    }

    private static class McOps {
        private final MinioClient mc;
        private final String testBucket;

        private final Function<String, RemoveObjectArgs> createRemoveArgs;
        private final Function<String, ListObjectsArgs>  createListObjArgs;
        private final Function<String, StatObjectArgs>   createStatsArgs;

        private McOps(MinioClient mc, String testBucket) {
            this.mc = mc;
            this.testBucket = testBucket;
            this.createRemoveArgs = key  -> RemoveObjectArgs.builder().bucket(testBucket).object(key).build();
            this.createListObjArgs = key -> ListObjectsArgs.builder().bucket(testBucket).prefix(key).build();
            this.createStatsArgs = key -> StatObjectArgs.builder().bucket(testBucket).object(key).build();
        }

        void removeBucket() {
            castRuntime(() -> mc.removeBucket(RemoveBucketArgs.builder().bucket(testBucket).build()));
        }

        void removeObject(String key) {
            castRuntime(() -> mc.removeObject(createRemoveArgs.apply(key)));
        }

        Stream<Item> listObjects(String key) {
            return Stream.of(castRuntime(() -> mc.listObjects(createListObjArgs.apply(key))))
                    .flatMap(elems -> StreamSupport.stream(elems.spliterator(), false))
                    .map(res -> castRuntime(res::get));
        }

        ObjectStat stats(String key) {
            return castRuntime(() -> mc.statObject(createStatsArgs.apply(key)));
        }

        private void castRuntime(VoidExceptional body) {
            try {
                body.execute();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private <R> R castRuntime(Exceptional<R> body) {
            try {
                return body.execute();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
