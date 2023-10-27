package fr.eseo.webcube.api.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @PostMapping("/upload")
    public String uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("directory") String directory) {
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
            return "File uploaded and extracted successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            return "An error occurred while processing the file: " + e.getMessage();
        }
    }
}

