package com.gelerion.open.storage.s3.provider;


import com.amazonaws.services.s3.AmazonS3;

public interface S3ClientFactory {
    AmazonS3 createS3Client(AwsConfig config);
}
