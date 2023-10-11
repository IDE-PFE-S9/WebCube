package fr.eseo.webcube.api;

import com.rabbitmq.client.*;

public class JobProducer {
    private final static String JOB_QUEUE_NAME = "jobs";
    private final static String RESULT_QUEUE_NAME = "results";
    private static int resultReceived = 0;
    private final static int NUMBER_OF_JOBS = 1;

    public static void main(String[] argv) throws Exception {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < NUMBER_OF_JOBS; i++) {
            sendJob();
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        System.out.println("Total execution time: " + elapsedTime + " milliseconds");
        retrieveResult();
        endTime = System.currentTimeMillis();
        elapsedTime = endTime - startTime;

        System.out.println("Total execution time: " + elapsedTime + " milliseconds");
    }

    public static void sendJob() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {
            channel.queueDeclare(JOB_QUEUE_NAME, false, false, false, null);
            String projectPath = "/Users/arthur/Library/Mobile Documents/com~apple~CloudDocs/Documents/ESEO/Cours-i3/S9/PFE/JavaTestProject";
            channel.basicPublish("", JOB_QUEUE_NAME, null, projectPath.getBytes());
            System.out.println(" [x] Sent job to compile and run");
        }
    }

    public static void retrieveResult() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {
            channel.queueDeclare(RESULT_QUEUE_NAME, false, false, false, null);
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String result = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received result: \n" + result);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                synchronized (JobProducer.class) {
                    resultReceived += 1; // Set flag to true when result is received
                    JobProducer.class.notifyAll(); // Notify waiting threads
                }
            };
            channel.basicConsume(RESULT_QUEUE_NAME, false, deliverCallback, consumerTag -> {
            });

            // Block until result is received
            synchronized (JobProducer.class) {
                while (resultReceived < NUMBER_OF_JOBS) {
                    JobProducer.class.wait();
                }
            }
        }
    }
}
