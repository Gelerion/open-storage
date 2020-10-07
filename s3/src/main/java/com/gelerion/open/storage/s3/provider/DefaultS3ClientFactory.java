package com.gelerion.open.storage.s3.provider;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class DefaultS3ClientFactory implements S3ClientFactory {

    @Override
    public AmazonS3 createS3Client(AwsConfig config) {
        AmazonS3ClientBuilder clientBuilder = AmazonS3ClientBuilder.standard();
        config.configure(clientBuilder);
        return clientBuilder.build();
    }
}
