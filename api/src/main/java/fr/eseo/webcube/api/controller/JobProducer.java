package fr.eseo.webcube.api.controller;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

@RestController
@RequestMapping("/api")
public class JobProducer {

    @GetMapping("/compileAndRun")
    public String compileAndRun(@RequestParam String projectPath, @RequestParam boolean isTest) throws Exception {
        String requestId = UUID.randomUUID().toString(); // Generate a unique request ID
        sendJob(projectPath, requestId, isTest);
        return retrieveResult(requestId);
    }

    public void sendJob(String projectPath, String requestId, boolean isTest) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {
            String exchangeName = "jobs_exchange";
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC);
            // Créez un objet JSON pour encapsuler les données que vous souhaitez envoyer
            JSONObject jobData = new JSONObject();
            jobData.put("projectPath", projectPath);
            jobData.put("isTest", isTest);


            // Convertissez l'objet JSON en une chaîne de caractères
            String messageBody = jobData.toString();

            channel.basicPublish(exchangeName, "jobs." + requestId, null, messageBody.getBytes());
            System.out.println(" [x] Sent job to compile and run");
        }
    }

    public String retrieveResult(String requestId) throws Exception {
        final String RESULT_EXCHANGE_NAME = "results_exchange";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        BlockingQueue<String> resultQueue = new ArrayBlockingQueue<>(1); // Queue to hold the result
        CountDownLatch latch = new CountDownLatch(1); // Latch to manage blocking/unblocking
    
        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(RESULT_EXCHANGE_NAME, "direct", true);
    
            // Declare an anonymous queue
            String anonymousQueueName = channel.queueDeclare().getQueue();
            channel.queueBind(anonymousQueueName, RESULT_EXCHANGE_NAME, requestId);
    
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String result = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received result: \n" + result);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                resultQueue.offer(result); // Put the result in the queue
                latch.countDown(); // Unblock the method
            };
    
            channel.basicConsume(anonymousQueueName, false, deliverCallback, consumerTag -> {});
    
            // Wait for the result, with a timeout to prevent infinite blocking
            boolean received = latch.await(5, TimeUnit.MINUTES); // Wait up to 5 minutes for the result
            if (received) {
                return resultQueue.poll(); // Retrieve the result from the queue
            } else {
                throw new Exception("Timed out waiting for result");
            }
        }
    }    
}
