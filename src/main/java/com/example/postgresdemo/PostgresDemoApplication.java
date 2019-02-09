package com.example.postgresdemo;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PostgresDemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(PostgresDemoApplication.class, args);
	}

	// Suspect needed
	@Autowired
	AmazonSQS sqs;

	@Bean
	public CommandLineRunner queue() {
		System.out.println("CLR called");


// https://medium.com/agorapulse-stories/how-to-unit-test-aws-services-with-localstack-and-testcontainers-1d39fe5dc6c2
//		AmazonSQS sqs = AmazonSQSClient
//				.builder()
//				.withEndpointConfiguration(localstack.getEndpointConfiguration(SQS))
//				.withCredentials(localstack.getDefaultCredentialsProvider()).build();


//		AwsClientBuilder.EndpointConfiguration endpoint =
//				new AwsClientBuilder.EndpointConfiguration("http://localhost:4576/", awsRegion.getName());
//		AmazonSQSClient client =  (AmazonSQSClient) AmazonSQSClientBuilder.standard()
//				.withCredentials(configuration.getSqsConfiguration().getCredentialsProvider())
//				.withEndpointConfiguration(endpoint)
//				.build();

		// Brutal
		AwsClientBuilder.EndpointConfiguration endpoint =
				new AwsClientBuilder.EndpointConfiguration("http://localhost:4576/", "us-east-1");

		// No credentials ...
		System.out.println("pre client");
		AmazonSQSClient client =null;
		System.out.println("Attempt to create client");
		try {
			client = (AmazonSQSClient) AmazonSQSClientBuilder.standard()
					.withEndpointConfiguration(endpoint)
					.build();
		} catch (Exception e) {
			System.out.println("post client");
			e.printStackTrace();
		}

		System.out.println("pre request");
		SendMessageRequest smr=new SendMessageRequest()
				.withQueueUrl("queue/test123")
				.withMessageBody("test from postgresDemo")
				.withDelaySeconds(1);
//		try{
//			smr.setMessageBody("Yo!");
//			smr.setQueueUrl( client.getQueueUrl("testing").toString());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		System.out.println("post SendMessageRequest");

		System.out.println("pre send");
		try{
			client.sendMessage(smr);
		} catch (Exception e) {
			System.out.println("post SendMessage");
			e.printStackTrace();
		}



		System.out.println(client.getQueueUrl( "testing"));



		// This seems to be what need to be returned (generally)
		return (args) -> {
			System.out.println("Run args : " + args );
		};
	}







}
