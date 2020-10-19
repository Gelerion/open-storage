package com.gelerion.open.storage.s3.test;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.gelerion.open.storage.api.Storage;
import com.gelerion.open.storage.api.domain.StorageDirectory;
import com.gelerion.open.storage.api.domain.StorageFile;
import com.gelerion.open.storage.s3.S3Storage;
import com.gelerion.open.storage.s3.domain.S3StorageFile;
import com.gelerion.open.storage.s3.provider.AwsClientsProvider;
import com.gelerion.open.storage.s3.provider.AwsConfig;
import com.gelerion.open.storage.test.StorageIntegrationTest;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.StatObjectArgs;
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

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    private MinioClient mc;
    private S3Storage s3Storage;

    @BeforeAll
    public void init() {
        String minioServerEndpoint = format("http://%s:9000", MINIO.getContainerIpAddress());

        this.mc = MinioClient.builder()
                .credentials(ACCESS_KEY, SECRET_KEY)
                .endpoint(minioServerEndpoint)
                .build();

        ClientConfiguration clientConf = new ClientConfiguration()
                .withSignerOverride("AWSS3V4SignerType");

        EndpointConfiguration endpointConf = new EndpointConfiguration(minioServerEndpoint, Regions.US_EAST_1.name());

        AwsConfig awsConfig = new AwsConfig()
                .withEndpointConfig(endpointConf)
                .withClientConfig(clientConf)
                .withCredentials(ACCESS_KEY, SECRET_KEY)
                .enablePathStyleAccess();

        this.s3Storage = S3Storage.newS3Storage(new AwsClientsProvider(awsConfig));
        super.init();
    }

//    @Test
//    void name() throws Exception {
//        System.out.println("All buckets:");
//        mc.makeBucket(MakeBucketArgs.builder().bucket("denis").build());
//        mc.listBuckets().stream().map(Bucket::name).forEach(System.out::println);
//
//        s3Storage.dirs(null).forEach(System.out::println);
//
//        System.out.println("abc");
//    }

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
        return null;
    }

    @Override
    public void assertFileExist(StorageFile file) throws IOException {
        try {
            S3StorageFile s3File = (S3StorageFile) file;

            StatObjectArgs objectArgs = StatObjectArgs
                    .builder()
                    .bucket(testBucket)
                    .object(s3File.key())
                    .build();

            ObjectStat objectStat = mc.statObject(objectArgs);
            assertEquals(file.name(), objectStat.name());
        } catch (Exception e) {
            System.out.println("File " + file + " does not exist!");
            throw new IOException(e);
        }
    }

    @Override
    public void assertFileSizeEqualsTo(StorageFile storageFile, long size) throws IOException {

    }

    @Override
    public void assertFileHasContent(Collection<String> lines) throws IOException {

    }

    @Override
    public void assertDirExist(StorageDirectory dir) throws IOException {

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
}
