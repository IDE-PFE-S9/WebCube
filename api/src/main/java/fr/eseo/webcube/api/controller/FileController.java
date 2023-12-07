package fr.eseo.webcube.api.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @PostMapping("/upload")
    public String uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("directory") String directory,
            @RequestHeader(name = "Authorization-Azure") String tokenAzure,
            @RequestHeader(name = "Authorization-API") String tokenApi) {
        try {
            // Validate the directory path
            Path dirPath = Paths.get(directory);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // Open the ZIP file
            try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(file.getBytes()))) {
                ZipEntry zipEntry;
                while ((zipEntry = zis.getNextEntry()) != null) {
                    System.out.println(zipEntry);
                    File entryDestination = new File(directory, zipEntry.getName());
                    if (zipEntry.isDirectory()) {
                        entryDestination.mkdirs();
                    } else {
                        entryDestination.getParentFile().mkdirs();
                        try (OutputStream os = new FileOutputStream(entryDestination)) {
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = zis.read(buffer)) > 0) {
                                os.write(buffer, 0, length);
                            }
                        }
                    }
                }
            }

            // commit the changes to the gir repository
            try (Git git = Git.open(new File(directory))) {
                // Check if the branch exists and create it if not
                String branchName = "arthurmeyniel";
                List<Ref> branches = git.branchList().call();
                boolean branchExists = branches.stream().anyMatch(ref -> ref.getName().endsWith(branchName));

                if (!branchExists) {
                    git.branchCreate().setName(branchName).call();
                }

                // Checkout the branch (newly created or existing)
                git.checkout().setName(branchName).call();

                // Add all files to the repository
                git.add().addFilepattern(".").call();

                // Commit the changes
                git.commit().setMessage("test commit message").call();

                // Push the commit to the branch
                // TODO: GET ANOTHER GITLAB ACCOUNT
                git.push()
                        .setCredentialsProvider(
                                new UsernamePasswordCredentialsProvider("meyniear", "glpat-REWFnLwzzczXAstaQGNU"))
                        .setRefSpecs(new RefSpec(branchName))
                        .call();
            } catch (IOException | GitAPIException e) {
                e.printStackTrace();
                // Handle exceptions
            }
            System.out.println("yes");
            return "File uploaded and extracted successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            return "An error occurred while processing the file: " + e.getMessage();
        }
    }
}
