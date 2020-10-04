package com.gelerion.open.storage.s3.provider.factory;


import com.amazonaws.services.s3.AmazonS3;
import com.gelerion.open.storage.s3.provider.AwsConfig;

public interface S3ClientFactory {
    AmazonS3 createS3Client(AwsConfig config);
}
