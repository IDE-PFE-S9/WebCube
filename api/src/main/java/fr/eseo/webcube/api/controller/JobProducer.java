package fr.eseo.webcube.api.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
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

    @Value("${spring.rabbitmq.host}")
    private String host;

    // TODO: fix the paths to use the token.

    @GetMapping("/compileAndRun")
    public String compileAndRun(@RequestParam String projectPath,
            @RequestHeader(name = "Authorization-Azure") String tokenAzure,
            @RequestHeader(name = "Authorization-API") String tokenApi) throws Exception {
        String requestId = UUID.randomUUID().toString(); // Generate a unique request ID
        sendJob(projectPath, requestId, "run");
        return retrieveResult(requestId);
    }

    @GetMapping("/compileAndTest")
    public String compileAndTest(@RequestParam String projectPath,
            @RequestHeader(name = "Authorization-Azure") String tokenAzure,
            @RequestHeader(name = "Authorization-API") String tokenApi) throws Exception {
        String requestId = UUID.randomUUID().toString(); // Generate a unique request ID
        sendJob(projectPath, requestId, "test");
        return retrieveResult(requestId);
    }

    @GetMapping("/compileAndJar")
    public ResponseEntity<Resource> compileAndJar(@RequestParam String projectPath) throws Exception {
        String requestId = UUID.randomUUID().toString(); // Generate a unique request ID
        sendJob(projectPath, requestId, "jar");
        String jarFilePath = retrieveResult(requestId); // This retrieves the path of the JAR file

        Path path = Paths.get(jarFilePath);
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + path.getFileName().toString())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    public void sendJob(String projectPath, String requestId, String action) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {
            String exchangeName = "jobs_exchange";
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC);
            // Créez un objet JSON pour encapsuler les données que vous souhaitez envoyer
            // TODO: change the json to jackson to ensure type safety
            JSONObject jobData = new JSONObject();
            jobData.put("projectPath", projectPath);
            jobData.put("action", action);

            // Convertissez l'objet JSON en une chaîne de caractères
            String messageBody = jobData.toString();

            channel.basicPublish(exchangeName, "jobs." + requestId, null, messageBody.getBytes());
            System.out.println(" [x] Sent job to compile and run");
        }
    }

    public String retrieveResult(String requestId) throws Exception {
        final String RESULT_EXCHANGE_NAME = "results_exchange";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
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

            channel.basicConsume(anonymousQueueName, false, deliverCallback, consumerTag -> {
            });

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
