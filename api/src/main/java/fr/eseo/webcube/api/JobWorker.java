package fr.eseo.webcube.api;

import com.rabbitmq.client.*;
import java.nio.file.*;
import java.io.*;

public class JobWorker {
    private final static String JOB_QUEUE_NAME = "jobs";
    private final static String RESULT_QUEUE_NAME = "results";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(JOB_QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for jobs.");
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                    AMQP.BasicProperties properties, byte[] body) throws IOException {
                long deliveryTag = envelope.getDeliveryTag();
                try {
                    String projectPath = new String(body, "UTF-8");
                    System.out.println(" [x] Received '" + projectPath + "'"); // Additional log
                    compileAndRun(projectPath, channel);
                    channel.basicAck(deliveryTag, false); // Manual acknowledgment
                    System.out.println(" [x] Acknowledged"); // Acknowledgment log
                } catch (Exception e) {
                    System.err.println(" [!] Error processing message: " + e.getMessage());
                    e.printStackTrace();
                    // Optionally, send a negative acknowledgment (basicNack) to requeue the message
                    channel.basicNack(deliveryTag, false, true);
                }
            }
        };
        channel.basicConsume(JOB_QUEUE_NAME, false, consumer);
    }

    private static void compileAndRun(String projectPath, Channel channel) {
        long startTime = System.currentTimeMillis();
        try {
            File projectDir = new File(projectPath);
            File srcDir = new File(projectDir, "src");
            File libDir = new File(projectDir, "lib");
            File classesDir = new File(projectDir, "classes");
            classesDir.mkdir(); // Create classes directory to hold compiled classes

            // Build the classpath from the jars in the lib directory
            StringBuilder classpath = new StringBuilder();
            for (File file : libDir.listFiles()) {
                if (file.getName().endsWith(".jar")) {
                    classpath.append(file.getAbsolutePath()).append(File.pathSeparator);
                }
            }

            // Search for Main.java in the src directory
            Path mainJavaPath = Files.walk(srcDir.toPath())
                    .filter(path -> path.getFileName().toString().equals("Main.java"))
                    .findFirst()
                    .orElseThrow(() -> new FileNotFoundException("Main.java not found"));

            // Compile the source files
            Process compileProcess = new ProcessBuilder(
                    "javac",
                    "-d", classesDir.getAbsolutePath(),
                    "-cp", classpath.toString(),
                    "-sourcepath", srcDir.getAbsolutePath(),
                    mainJavaPath.toString()).directory(projectDir).start();
            int compileExitCode = compileProcess.waitFor();
            String compilationErrors = logProcessOutput("Compilation", compileProcess);

            if (compileExitCode != 0) {
                System.err.println("Compilation failed with exit code " + compileExitCode);
                sendOutput(compilationErrors, channel);
                return; // Exit early if compilation failed
            }

            // Run the compiled project
            String mainClass = mainJavaPath.toString()
                    .substring(srcDir.getAbsolutePath().length() + 1)
                    .replace(".java", "")
                    .replace(File.separator, ".");
            Process runProcess = new ProcessBuilder(
                    "java",
                    "-cp", classpath.append(classesDir.getAbsolutePath()).toString(),
                    mainClass).directory(projectDir).start();

            StringBuilder output = new StringBuilder();
            StringBuilder error = new StringBuilder();

            try (BufferedReader outputReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                    BufferedReader errorReader = new BufferedReader(
                            new InputStreamReader(runProcess.getErrorStream()))) {

                String line;
                while ((line = outputReader.readLine()) != null) {
                    output.append(line).append(System.lineSeparator());
                }
                while ((line = errorReader.readLine()) != null) {
                    error.append(line).append(System.lineSeparator());
                }
            }

            int runExitCode = runProcess.waitFor();
            if (runExitCode != 0) {
                System.err.println("Run failed with exit code " + runExitCode);
                System.err.println("Error output: " + error.toString());
            } else {
                System.out.println("Run succeeded with output: \n");
            }

            // Optionally send the output to the "results" queue
            String result = "Compilation Errors:\n" + compilationErrors + "\nOutput:\n" + output.toString() + "\nErrors:\n" + error.toString();
            sendOutput(result, channel);

            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
    
            System.out.println("Total execution time: " + elapsedTime + " milliseconds");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String logProcessOutput(String processName, Process process) throws IOException {
        StringBuilder processOutput = new StringBuilder();
        StringBuilder processErrors = new StringBuilder();
        
        System.out.println(processName + " output:");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                processOutput.append(line).append(System.lineSeparator());
            }
        }
        
        System.out.println(processName + " errors:");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.err.println(line);
                processErrors.append(line).append(System.lineSeparator());
            }
        }
        
        return processErrors.toString();
    }

    private static void sendOutput(String output, Channel channel) throws Exception {
        channel.queueDeclare(RESULT_QUEUE_NAME, false, false, false, null);
        channel.basicPublish("", RESULT_QUEUE_NAME, null, output.getBytes());
        System.out.println(" [x] Sent result");
    }
}