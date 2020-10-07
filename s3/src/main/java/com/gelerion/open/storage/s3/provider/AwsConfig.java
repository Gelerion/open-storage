package com.gelerion.open.storage.s3.provider;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.util.Objects;

public class AwsConfig {

    private boolean accelerateModeEnabled;
    private boolean payloadSigningEnabled;
    private boolean dualstackEnabled;
    private boolean forceGlobalBucketAccessEnabled;
    private boolean pathStyleAccessEnabled;

    private ClientConfiguration clientConfig;
    private EndpointConfiguration endpointConfig;
    private AWSCredentialsProvider credentialsProvider;

    public AwsConfig withClientConfig(ClientConfiguration clientConfig) {
        this.clientConfig = clientConfig;
        return this;
    }

    public AwsConfig withEndpointConfig(EndpointConfiguration endpointConfig) {
        this.endpointConfig = endpointConfig;
        return this;
    }

    public AwsConfig withCustomCredentialsProvider(AWSCredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
        return this;
    }

    public AwsConfig withCredentials(String accessKey, String secretKey) {
        Objects.requireNonNull(accessKey);
        Objects.requireNonNull(secretKey);
        withCustomCredentialsProvider(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)));
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

    public AwsConfig enablePathStyleAccess() {
        this.pathStyleAccessEnabled = true;
        return this;
    }

    AmazonS3ClientBuilder configure(AmazonS3ClientBuilder builder) {
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
        if (this.pathStyleAccessEnabled) {
            builder.enablePathStyleAccess();
        }
        if (this.clientConfig != null) {
            builder.setClientConfiguration(clientConfig);
        }
        if (this.endpointConfig != null) {
            builder.setEndpointConfiguration(endpointConfig);
        }
        if (this.credentialsProvider != null) {
            builder.setCredentials(credentialsProvider);
        }

        return builder;
    }

}
