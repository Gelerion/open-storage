package com.gelerion.open.storage.s3.provider;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class AwsConfig {

    private Boolean accelerateModeEnabled;
    private Boolean payloadSigningEnabled;
    private Boolean dualstackEnabled;
    private Boolean forceGlobalBucketAccessEnabled;
    private ClientConfiguration clientConfig;
    private AWSCredentialsProvider credentialsProvider;

    public AwsConfig withClientConfig(ClientConfiguration clientConfig) {
        this.clientConfig = clientConfig;
        return this;
    }

    public AwsConfig withCustomCredentialsProvider(AWSCredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
        return this;
    }

    public AwsConfig enableAccelerateMode() {
        this.accelerateModeEnabled = true;
        return this;
    }

    public AwsConfig enableDualstack() {
        this.dualstackEnabled = true;
        return this;
    }

    public AwsConfig enableForceGlobalBucketAccess() {
        this.forceGlobalBucketAccessEnabled = true;
        return this;
    }

    public AwsConfig enablePayloadSigning() {
        this.payloadSigningEnabled = true;
        return this;
    }

    public AmazonS3ClientBuilder configure(AmazonS3ClientBuilder builder) {
        if (this.payloadSigningEnabled) {
            builder.enablePayloadSigning();
        }
        if (this.accelerateModeEnabled) {
            builder.enableAccelerateMode();
        }
        if (this.dualstackEnabled) {
            builder.enableDualstack();
        }
        if (this.forceGlobalBucketAccessEnabled) {
            builder.enableForceGlobalBucketAccess();
        }
        if (this.clientConfig != null) {
            builder.setClientConfiguration(clientConfig);
        }
        if (this.credentialsProvider != null) {
            builder.setCredentials(credentialsProvider);
        }

        return builder;
    }

}
