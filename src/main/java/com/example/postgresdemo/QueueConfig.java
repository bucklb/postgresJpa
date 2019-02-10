package com.example.postgresdemo;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * components we'll need
 */
@Configuration
public class QueueConfig {

    String serviceEndpoint = "http://localhost:4576/";
    String signingRegion   = "us-east-1";
    String accessKey = "accessKey";
    String secretKey = "secretKey";
    String queueUrl  = "queue/testing";

    // TODO : AmazonSQSclient or AmazonSQS better option
    @Bean
    public AmazonSQSClient sqsClient() {

        System.out.println("AmazonSQSClient");


        // Endpoint
        AwsClientBuilder.EndpointConfiguration endpoint =
                new AwsClientBuilder.EndpointConfiguration(
                        serviceEndpoint,
                        signingRegion);

        // Security credentials
        AWSCredentialsProvider creds =
                new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(
                                accessKey,
                                secretKey
                        )
                );

        // Build the client
        AmazonSQSClient client =null;
            client = (AmazonSQSClient) AmazonSQSClientBuilder.standard()
                    .withEndpointConfiguration(endpoint)
                    .withCredentials(creds)
                    .build();

        return client;
    }




}
