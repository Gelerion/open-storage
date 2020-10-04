package com.gelerion.open.storage.s3.provider;

import com.amazonaws.services.s3.AmazonS3;
import com.gelerion.open.storage.s3.provider.factory.DefaultS3ClientFactory;
import com.gelerion.open.storage.s3.provider.factory.S3ClientFactory;

public class AwsClientsProvider {
    private AwsConfig config;
    private S3ClientFactory s3ClientFactory;

    public static AwsClientsProvider getDefault() {
        return new AwsClientsProvider(new AwsConfig());
    }

    public AwsClientsProvider(AwsConfig config) {
        this.config = config;
        this.s3ClientFactory = new DefaultS3ClientFactory();
    }


    public AmazonS3 s3Client() {
        return this.s3ClientFactory.createS3Client(config);
    }

}
