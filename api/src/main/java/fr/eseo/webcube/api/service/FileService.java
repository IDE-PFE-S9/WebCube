package fr.eseo.webcube.api.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import fr.eseo.webcube.api.Response.FileUploadResponse;

@Service
public class FileService {

    private static final String DOWNLOAD_DIRECTORY = "main/downloadFile";

    public ResponseEntity<FileUploadResponse> uploadFile(MultipartFile multipartFile, String fileName)
            throws IOException {

        long size = multipartFile.getSize(); // en octets
        String originalFileName = multipartFile.getOriginalFilename();
        
        // Vérifier si le nom de fichier se termine par ".java"
        if (originalFileName != null && originalFileName.toLowerCase().endsWith(".java")) {
            // Le fichier est un fichier Java
            String type = multipartFile.getContentType();
        
            if (type == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        
        } else {
            // Le fichier n'est pas un fichier Java
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
                

        String filecode = saveFile(fileName, multipartFile);

        FileUploadResponse response = new FileUploadResponse();
        response.setFileName(fileName);
        response.setSize(size);
        response.setDownloadUri("/downloadFile/" + filecode);

        //log.info("File {} uploaded successfully", fileName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private String saveFile(String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(DOWNLOAD_DIRECTORY);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException("Impossible de sauvegarder le fichier : " + fileName, ioe);
        }

        return fileName;
    }
    
}
