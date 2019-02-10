package com.example.postgresdemo.service;


import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueueService {

    // Will need sqs functionality
    @Autowired
    AmazonSQSClient sqsClient;

    String QUEUE_PREFIX = "queue/";


    /**
     * initial point to instance things
     */
    public void doSQS() {

        System.out.println("doSQS");
    }

    /**
     * Just attempt the queue creation regardless for now, but should be able to check the existing
     * @param queueName
     */
    protected void createQueue( String queueName ) {

        CreateQueueRequest createRequest=new CreateQueueRequest( queueName )
                .addAttributesEntry("MessageRetentionPeriod", "86400");

        try {

            sqsClient.createQueue(createRequest);
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    /**
     * Given message and where to write it, do so
     * @param messageText
     * @param queueName
     */
    public void sendMessage( String messageText, String queueName ) {

        // TODO : improve getting the queue Url
        String queueUrl=QUEUE_PREFIX + queueName;

        // Perhaps check for queue existence (or force creation)
        createQueue( queueName );

        // Generate the request.  Must be a better way than having queue as text in the calls
        SendMessageRequest smr=new SendMessageRequest()
                .withQueueUrl( queueUrl )
                .withMessageBody( messageText )
                .withDelaySeconds( 0 );

        // send it to queue
        sqsClient.sendMessage( smr );

    }

    /**
     * For now just grab something from the queue, dump to screen and then remove from queue
     * @param queueName
     */
    public String getMessage( String queueName ){

        // TODO : improve getting the queue Url
        String queueUrl = QUEUE_PREFIX + queueName;
        String messageText = "";

        List<Message> messages = sqsClient.receiveMessage( queueUrl ).getMessages();
        for (Message m : messages) {

            messageText = m.getBody();
            System.out.println( "Message body was -> " + messageText );

            sqsClient.deleteMessage(queueUrl, m.getReceiptHandle());
        }

        return messageText;
    }








}
