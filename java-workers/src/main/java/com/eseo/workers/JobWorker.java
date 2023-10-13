package com.eseo.workers;

import com.rabbitmq.client.*;

import java.nio.channels.Channel;
import java.nio.file.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.io.*;

public class JobWorker {
    private static final String RESULT_EXCHANGE_NAME = "results_exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("172.20.0.2"); // Adapt to config, set RabbitMQ server IP
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String exchangeName = "jobs_exchange";
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, exchangeName, "jobs.*");

        System.out.println(" [*] Waiting for jobs.");
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                    AMQP.BasicProperties properties, byte[] body) throws IOException {
                String routingKey = envelope.getRoutingKey();
                String requestId = routingKey.substring(routingKey.indexOf('.') + 1);
                long deliveryTag = envelope.getDeliveryTag();
                try {
                    // Convertissez le corps (body) en une chaîne de caractères
                    String messageBody = new String(body, "UTF-8");

                    // Analysez la chaîne JSON en un objet JSON
                    JSONObject jobData = new JSONObject(messageBody);

                    // Extrait les valeurs du JSON
                    String projectPath = jobData.getString("projectPath");
                    boolean isTest = jobData.getBoolean("isTest");

                    System.out.println(" [x] Received '" + projectPath + "'"); // Additional log
                    if (isTest){
                        compileTestAndRun(projectPath, requestId, channel);
                    }
                    else{
                        // Compilez et exécutez le projet (Main.java
                        compileAndRun(projectPath, requestId, channel);
                    }
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
        channel.basicConsume(queueName, false, consumer);
    }

    private static void compileAndRun(String projectPath, String requestId, Channel channel) {
        long startTime = System.currentTimeMillis();
        long endTime;
        long compilationTime, runTime;
        String result;
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
            endTime = System.currentTimeMillis();
            compilationTime = endTime - startTime;
            String compilationErrors = logProcessOutput("Compilation", compileProcess);

            if (compileExitCode != 0) {
                System.err.println("Compilation failed with exit code " + compileExitCode);
                result = "Compilation failed with exit code " + compileExitCode + "\n"
                        + "Output: \n" + compilationErrors + "\n"
                        + "Total execution time: " + compilationTime + " ms";
                sendOutput(result, requestId, channel);
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

            endTime = System.currentTimeMillis();
            runTime = endTime - startTime;

            if (runExitCode != 0) {
                System.err.println("Run failed with exit code " + runExitCode);
                System.err.println("Error output: " + error.toString());
                result = "Compilation successful (" + compilationTime + " ms)\n"
                        + "Run failed with exit code " + runExitCode + "\n"
                        + "Output: \n" + output.toString() + error.toString() + "\n"
                        + "Total execution time: " + runTime + " milliseconds";
                sendOutput(result, requestId, channel);
            } else {
                System.out.println("Run succeeded with output: \n");
                System.out.println(output.toString());
                result = "Compilation successful (" + compilationTime + " ms)\n"
                        + "Run succeed with exit code " + runExitCode + "\n"
                        + "Output: \n" + output.toString() + "\n"
                        + "Total execution time: " + runTime + " milliseconds";
            }

            sendOutput(result, requestId, channel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void compileTestAndRun(String projectPath, String requestId, Channel channel) {
        long startTime = System.currentTimeMillis();
        long endTime;
        long compilationTime, runTime;
        String result;
        try {
            File projectDir = new File(projectPath);
            File srcDir = new File(projectDir, "src");
            File libDir = new File(projectDir, "lib");
            File classesDir = new File(projectDir, "classes");
            File testDir = new File(projectDir, "test");
            File classesTestDir = new File(projectDir, "classesTest");
            classesDir.mkdir(); // Créez le répertoire des classes pour les classes compilées
            classesTestDir.mkdir(); // Créez le répertoire des classes pour les classes de test compilées
    
            // Construisez le classpath à partir des fichiers JAR dans le répertoire lib
            StringBuilder classpath = new StringBuilder();
            for (File file : libDir.listFiles()) {
                if (file.getName().endsWith(".jar")) {
                    classpath.append(file.getAbsolutePath()).append(File.pathSeparator);
                }
            }

            // Recherchez les fichiers source de test dans le répertoire "test"
            Path testPath = testDir.toPath();
            List<Class<?>> testClasses = findTestClasses(testPath); //creer methode findTestClasses()
    
            // Compilez les fichiers source des classes de test
            for (Class<?> testClass : testClasses) {
                String testClassName = testClass.getName();
                String testClassPath = testClassName.replace(".", File.separator) + ".java";
                String testClassPathFull = testPath.toString() + File.separator + testClassPath;

                Process compileTestProcess = new ProcessBuilder(
                    "javac",
                    "-d", classesTestDir.getAbsolutePath(),
                    "-cp", classpath.toString(),
                    testClassPathFull).directory(projectDir).start();

                int compileTestExitCode = compileTestProcess.waitFor();
                // Gérez la compilation des classes de test de la même manière que pour le code source principal
            }
    
            // Exécutez les classes de test avec JUnit
            JUnitCore junit = new JUnitCore();
            Result junitResult = junit.run(testClasses.toArray(new Class<?>[0]));
    
            for (Failure failure : junitResult.getFailures()) {
                System.err.println(failure.toString());
            }
    
            if (junitResult.wasSuccessful()) {
                System.out.println("Tous les tests ont réussi.");
            }
    
            // Exécutez le code principal (Main) ici (le code source principal) comme vous l'avez fait précédemment.
    
            // ...
    
            // Enfin, envoyez le résultat de la compilation et de l'exécution au client.
            endTime = System.currentTimeMillis();
            compilationTime = endTime - startTime;
    
            // Construisez le résultat final ici
            result = "Compilation réussie (" + compilationTime + " ms)\n";
            // Ajoutez d'autres informations au résultat si nécessaire.
    
            sendOutput(result, requestId, channel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Class<?>> findTestClasses(Path testPath) {
        List<Class<?>> testClasses = new ArrayList<>();
    
        try {
            // Parcourez le répertoire testPath pour trouver tous les fichiers .java.
            Files.walk(testPath)
                 .filter(path -> path.toString().endsWith(".java"))
                 .forEach(filePath -> {
                     try {
                         String relativePath = testPath.relativize(filePath).toString();
                         String className = relativePath.replace(".java", "").replace(File.separator, ".");
                         Class<?> clazz = Class.forName(className);
    
                         // Vérifiez si la classe a l'annotation @Test
                         if (clazz.isAnnotationPresent(Test.class)) {
                             testClasses.add(clazz);
                         }
                     } catch (ClassNotFoundException e) {
                         // Gérez les erreurs de classe introuvable
                         e.printStackTrace();
                     }
                 });
        } catch (IOException e) {
            // Gérez les erreurs d'E/S lors de la recherche des fichiers .java
            e.printStackTrace();
        }
    
        return testClasses;
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

    private static void sendOutput(String output, String requestId, Channel channel) throws Exception {
        channel.exchangeDeclare(RESULT_EXCHANGE_NAME, "direct", true);
        channel.basicPublish(RESULT_EXCHANGE_NAME, requestId, null, output.getBytes());
        System.out.println(" [x] Sent result with routing key: " + requestId);
    }
}
