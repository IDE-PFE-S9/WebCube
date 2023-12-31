package fr.eseo.webcube.api.workers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Stream;


public class TestRunner {

    private static final String LIB_DIR = "lib";
    private static final String SRC_DIR = "src/main";
    private static final String TEST_DIR = "src/test";
    private static final String CLASSES_DIR = "classes";
    private static final String CLASSES_SRC_DIR = CLASSES_DIR + "/main";
    private static final String CLASSES_TEST_DIR = CLASSES_DIR + "/test";

    public static class TestResult {
        private final String testName;
        private final String status;
        private final String data;

        private TestResult(Builder builder) {
            this.testName = builder.testName;
            this.status = builder.status;
            this.data = builder.data;
        }

        public String getTestName() {
            return testName;
        }

        public String getStatus() {
            return status;
        }

        public String getData() {
            return data;
        }

        @Override
        public String toString() {
            return "Test: " + testName + ", Status: " + status + ", Data: " + data;
        }

        public static class Builder {
            private String testName;
            private String status;
            private String data;

            public Builder testName(String testName) {
                this.testName = testName;
                return this;
            }

            public Builder status(String status) {
                this.status = status;
                return this;
            }

            public Builder data(String data) {
                this.data = data;
                return this;
            }

            public TestResult build() {
                return new TestResult(this);
            }
        }
    }

    public static List<TestResult> runTests(String classpath, File classesTestDir, File classesSrcDir)
            throws IOException, InterruptedException {
        List<String> outputLines = new ArrayList<>();

        List<String> command = new ArrayList<>();
        command.add("java");
        command.add("-jar");
        command.add("/Users/arthur/Library/Mobile Documents/com~apple~CloudDocs/Documents/ESEO/Cours-i3/S9/PFE/WebCube/api/src/main/java/fr/eseo/webcube/api/workers/junit-platform-console-standalone.jar");
        command.add("--class-path");
        command.add(classesSrcDir.getAbsolutePath() + File.pathSeparator + classesTestDir.getAbsolutePath());
        command.add("--scan-class-path");

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // Redirect stderr to stdout
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                outputLines.add(line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            System.err.println("JUnit Platform Console Launcher exited with code: " + exitCode);
        }

        List<TestResult> testResults = parseTestResults(outputLines);

        return testResults;
    }

    public static List<TestResult> parseTestResults(List<String> outputLines) {
        List<TestResult> testResults = new ArrayList<>();

        int beginIndex = findFirstOccurrenceIndex(outputLines, "JUnit Jupiter");
        int endIndex = findFirstOccurrenceIndex(outputLines, "JUnit Vintage");
        int failureIndex = findFirstOccurrenceIndex(outputLines, "Failures");

        int testRunIndex = findFirstOccurrenceIndex(outputLines, "Test run finished after");
        int testSucessfulIndex = testRunIndex + 11;
        int testFailedIndex = testRunIndex + 12;
        List<Integer> failureIndices = new ArrayList<>();

        if (failureIndex != -1) {
            failureIndices = findAllOccurrencesIndices(outputLines, "JUnit Jupiter:");
            failureIndices.add(testRunIndex);
        }

        System.out.println(failureIndices);

        String currentClassName = "";

        int k = 0;

        for (int i = beginIndex + 1; i < endIndex; i++) {
            String line = outputLines.get(i);
            if (line.contains("()")) {
                String testName = extractMethodName(line);

                // System.out.println(currentClassName + "." + testName + "()");
                if (line.contains("✘")) {
                    String status = "FAILED";

                    String data = mergeStrings(outputLines, failureIndices.get(k), failureIndices.get(k + 1) - 1);
                    k += 1;

                    TestResult testResult = new TestResult.Builder()
                            .testName(currentClassName + "." + testName + "()")
                            .status(status)
                            .data(data)
                            .build();
                    testResults.add(testResult);
                } else {
                    String status = "SUCCESS";
                    TestResult testResult = new TestResult.Builder()
                            .testName(currentClassName + "." + testName + "()")
                            .status(status)
                            .build();
                    testResults.add(testResult);
                }

            } else {
                currentClassName = extractClassName(line);
            }
        }
        return testResults;
    }

    public static String removeAnsiEscapeCodes(String input) {
        return input.replaceAll("(\\x1b\\[[0-9;]*m|\\[0m)", "");
    }

    public static String extractClassName(String line) {
        int lastDashIndex = line.indexOf('─');
        int resultIndex = line.indexOf('✔');
        if (resultIndex == -1) {
            resultIndex = line.indexOf('✘');
        }

        String className = line.substring(lastDashIndex + 2, resultIndex);

        return removeAnsiEscapeCodes(className).trim(); // Trim any leading/trailing whitespace and return the result
    }

    public static String extractMethodName(String line) {
        int openParenIndex = line.indexOf('('); // Find the index of the opening parenthesis
        int lastDashIndex = line.indexOf('─');
        String methodName = line.substring(lastDashIndex + 2, openParenIndex);

        return removeAnsiEscapeCodes(methodName).trim(); // Trim any leading/trailing whitespace and return the result
    }

    public static int findFirstOccurrenceIndex(List<String> lines, String substring) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains(substring)) {
                return i; // return the index of the string containing the substring
            }
        }
        return -1; // return -1 if no such string was found
    }

    public static List<Integer> findAllOccurrencesIndices(List<String> lines, String substring) {
        List<Integer> occurrencesIndices = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains(substring)) {
                occurrencesIndices.add(i);  // add the index of the string containing the substring to the list
            }
        }
        return occurrencesIndices;  // return the list of indices
    }    

    public static String mergeStrings(List<String> lines, int startIndex, int endIndex) {
        if (startIndex < 0 || endIndex >= lines.size() || startIndex > endIndex) {
            throw new IllegalArgumentException("Invalid indices");
        }
    
        StringBuilder mergedString = new StringBuilder();
        for (int i = startIndex; i <= endIndex; i++) {
            mergedString.append(lines.get(i));
            if (i < endIndex) {  // Avoid appending a newline after the last line
                mergedString.append("\n");
            }
        }
        
        return mergedString.toString();
    }

    private static String buildClasspath(File libDir, File... extraDirs) {
        StringBuilder classpath = new StringBuilder();
        for (File file : libDir.listFiles()) {
            if (file.getName().endsWith(".jar")) {
                classpath.append(file.getAbsolutePath()).append(File.pathSeparator);
            }
        }
        for (File extraDir : extraDirs) {
            classpath.append(extraDir.getAbsolutePath()).append(File.pathSeparator);
        }
        return classpath.toString();
    }

    private static void compileJavaFiles(File sourceDir, File outputDir, String classpath, File projectDir)
            throws IOException, InterruptedException {
        List<String> command = new ArrayList<>();
        command.add("javac");
        command.add("-d");
        command.add(outputDir.getAbsolutePath());
        command.add("-cp");
        command.add(classpath);
        try (Stream<Path> paths = Files.walk(sourceDir.toPath())) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .map(Path::toString)
                    .forEach(command::add);
        }
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(projectDir);
        Process compileProcess = processBuilder.start();
        int compileExitCode = compileProcess.waitFor();
        logProcessOutput("Compilation", compileProcess);
        if (compileExitCode != 0) {
            System.err.println("Compilation failed with exit code " + compileExitCode);
            // Handle compilation failure
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

    private static File createJarFile(File classesDir, File projectDir, File srcDir, File libDir)
            throws IOException, InterruptedException {

        Path mainJavaPath = Files.walk(srcDir.toPath())
                .filter(path -> path.getFileName().toString().equals("Main.java"))
                .findFirst()
                .orElseThrow(() -> new FileNotFoundException("Main.java not found"));

        // Derive the main class name from mainJavaPath
        String mainClass = mainJavaPath.toString()
                .substring(srcDir.getAbsolutePath().length() + 1)
                .replace(".java", "")
                .replace(File.separator, ".");

        // Define the path of the JAR file
        File jarFile = new File(projectDir, "output.jar");

        // Create a manifest with the main class
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, mainClass);

        // Create the JAR file
        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarFile), manifest)) {
            addDirectoryToJar(classesDir, jos, classesDir.getAbsolutePath().length() + 1);
    
            for (File file : libDir.listFiles()) {
                if (file.getName().endsWith(".jar")) {
                    try (JarFile libraryJar = new JarFile(file)) {
                        Enumeration<JarEntry> entries = libraryJar.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            if (!entry.isDirectory() && !entry.getName().equalsIgnoreCase("META-INF/MANIFEST.MF")) {
                                jos.putNextEntry(new JarEntry(entry.getName()));
                                try (InputStream is = libraryJar.getInputStream(entry)) {
                                    byte[] buffer = new byte[1024];
                                    int bytesRead;
                                    while ((bytesRead = is.read(buffer)) != -1) {
                                        jos.write(buffer, 0, bytesRead);
                                    }
                                }
                                jos.closeEntry();
                            }
                        }
                    }
                }
            }
        }

        return jarFile;
    }

    // Helper method to add a directory to a JAR output stream
    private static void addDirectoryToJar(File directory, JarOutputStream jos, int prefixLength)
            throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    addDirectoryToJar(file, jos, prefixLength);
                } else {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        String name = file.getAbsolutePath().substring(prefixLength).replace(File.separatorChar, '/');
                        JarEntry entry = new JarEntry(name);
                        jos.putNextEntry(entry);
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            jos.write(buffer, 0, bytesRead);
                        }
                        jos.closeEntry();
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // Example usage:
        String projectPath = "/Users/arthur/Library/Mobile Documents/com~apple~CloudDocs/Documents/ESEO/Cours-i3/S9/PFE/JavaTestProject";

        File projectDir = new File(projectPath);
        File libDir = new File(projectDir, LIB_DIR);
        File srcDir = new File(projectDir, SRC_DIR);
        File testDir = new File(projectDir, TEST_DIR);
        File classesSrcDir = new File(projectDir, CLASSES_SRC_DIR);
        File classesTestDir = new File(projectDir, CLASSES_TEST_DIR);
        classesSrcDir.mkdirs(); // Create classes/src directory
        classesTestDir.mkdirs(); // Create classes/test directory

        String classpath = buildClasspath(libDir, classesSrcDir, classesTestDir);

        compileJavaFiles(srcDir, classesSrcDir, classpath, projectDir);
        compileJavaFiles(testDir, classesTestDir, classpath, projectDir);

        createJarFile(classesSrcDir, projectDir, srcDir, libDir);

        // List<TestResult> testResults = runTests(classpath, classesTestDir, classesSrcDir);

        // for (TestResult testResult : testResults) {
        //     System.out.println(testResult);
        // }
    }
}
