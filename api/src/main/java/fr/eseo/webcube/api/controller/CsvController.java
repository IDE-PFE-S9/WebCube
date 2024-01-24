package fr.eseo.webcube.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import fr.eseo.webcube.api.service.CsvService;

import java.nio.charset.Charset;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;


@RestController
@RequestMapping("/api/csv")
public class CsvController {

    @Autowired
    private CsvService csvService;

    @PostMapping("/import")
    public ResponseEntity<String> importCsv(@RequestHeader(name = "Authorization-API") String tokenApi, @RequestParam("file") MultipartFile file) {
        try {
            // Vérifier si le fichier a l'extension CSV
            if (!file.getOriginalFilename().endsWith(".csv")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Le fichier doit être au format CSV.");
            }
            // Analyser le fichier CSV
            CSVParser parser = CSVParser.parse(file.getInputStream(), Charset.defaultCharset(), CSVFormat.DEFAULT);
            for (CSVRecord csvRecord : parser) {
                // Traiter chaque enregistrement CSV
                String unique_name = csvRecord.get(0);
                String firstName = csvRecord.get(1);
                String surName = csvRecord.get(2);
                String roles = csvRecord.get(3);
                csvService.importCsv(unique_name, firstName, surName, roles);

            }
            return ResponseEntity.ok("Import CSV réussi");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'import CSV");
        }
    }
    
}
