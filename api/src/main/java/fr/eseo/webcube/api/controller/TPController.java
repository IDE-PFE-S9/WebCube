package fr.eseo.webcube.api.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.eseo.webcube.api.model.TP;
import fr.eseo.webcube.api.service.TPService;

@RestController
@RequestMapping("/api")
public class TPController {

    @Autowired
    private TPService tpService;

    @GetMapping("/tp")
    public ResponseEntity<List<TP>> getTpList() {
        List<TP> tpList = tpService.getTpList();

        if (tpList != null && !tpList.isEmpty()) {
            return ResponseEntity.ok(tpList);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.emptyList());
        }
    }

    @GetMapping("/tp/{id}")
    public ResponseEntity<?> getTPById(@PathVariable Integer id) {
        Optional<TP> tpOptional = tpService.getTp(id);

        if (tpOptional.isPresent()) {
            return ResponseEntity.ok(tpOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/tp/archive/{id}")
    public ResponseEntity<Resource> getArchiveById(@PathVariable Integer id) {
        try {
            Resource archive = tpService.getArchive(id);
    
            String filename = "archive-" + id + ".wc"; // Set a filename for the downloaded archive
    
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/zip")) // Set the content type
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"") // Suggest download
                    .body(archive);
        } catch (Exception e) { // Correct exception handling
            System.err.println(e);
            return ResponseEntity.notFound().build();
        }
    }
}
