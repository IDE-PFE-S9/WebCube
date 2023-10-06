package fr.eseo.webcube.api.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import fr.eseo.webcube.api.Response.FileUploadResponse;
import fr.eseo.webcube.api.service.FileService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    @Autowired
    private final FileService fileService;


     /**
     * Uploads a file to the server.
     *
     * @param multipartFile The file to be uploaded as a {@link MultipartFile}.
     * @param name          The name of the file.
     * @return A {@link ResponseEntity} containing a {@link FileUploadResponse}
     *         object with the file upload details.
     * @throws IOException If there is an issue with file I/O during the upload
     *                     process.
     */
    @PostMapping("")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile multipartFile,
            @RequestParam("name") String name)
            throws IOException {
        return fileService.uploadFile(multipartFile, name);
    }
    
}
