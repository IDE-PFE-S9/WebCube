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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.eseo.webcube.api.Response.TpResponse;
import fr.eseo.webcube.api.model.TP;
import fr.eseo.webcube.api.security.JwtTokenUtil;
import fr.eseo.webcube.api.service.TPService;
import io.jsonwebtoken.Claims;

@RestController
@RequestMapping("/api")
public class TPController {

    @Autowired
    private TPService tpService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

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
    public ResponseEntity<?> getTPById(@PathVariable Integer id,
            @RequestHeader(name = "Authorization-API") String tokenApi) {
        Optional<TP> tpOptional = tpService.getTp(id);

        if (tpOptional.isPresent()) {
            return ResponseEntity.ok(tpOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/tp/archive/{id}")
    public ResponseEntity<Resource> getArchiveById(
            @PathVariable Integer id,
            @RequestHeader(name = "Authorization-API") String tokenApi) {
        try {
            tokenApi = tokenApi.substring(7);
            Claims claims = jwtTokenUtil.parseClaims(tokenApi);
            String uniqueName = claims.get("unique_name").toString();

            Resource archive = tpService.getArchive(id, uniqueName);

            String filename = "archive-" + id + ".wc"; // Set a filename for the downloaded archive

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/zip")) // Set the content type
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(archive);
        } catch (Exception e) { // Correct exception handling
            System.err.println(e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("tp/myCompletion")
    public ResponseEntity<TpResponse> getMyListCompletion(
            @RequestHeader(name = "Authorization-API") String tokenApi) {

        tokenApi = tokenApi.substring(7);
        Claims claims = jwtTokenUtil.parseClaims(tokenApi);
        String uniqueName = claims.get("unique_name").toString();
        String firstname = claims.get("firstname").toString();
        String surname = claims.get("surname").toString();

        TpResponse tpResponse = tpService.getCompletionByUniqueName(uniqueName);

        if (tpResponse != null) {
            tpResponse.setFirstname(firstname);
            tpResponse.setSurname(surname);
            return ResponseEntity.ok(tpResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
    }

    @GetMapping("tp/myCompletion/{id}")
    public ResponseEntity<TpResponse> getMyCompletion(
            @PathVariable Integer id,
            @RequestHeader(name = "Authorization-API") String tokenApi) {

        tokenApi = tokenApi.substring(7);
        Claims claims = jwtTokenUtil.parseClaims(tokenApi);
        String uniqueName = claims.get("unique_name").toString();
        String firstname = claims.get("firstname").toString();
        String surname = claims.get("surname").toString();

        TpResponse tpResponse = tpService.getCompletionByUniqueNameAndTpId(uniqueName, id);

        if (tpResponse != null) {
            tpResponse.setFirstname(firstname);
            tpResponse.setSurname(surname);
            return ResponseEntity.ok(tpResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
    }

    @GetMapping("tp/completion/etudiants/{id}")
    public ResponseEntity<List<TpResponse>> getCompletionMyTp(
            @PathVariable Integer id,
            @RequestHeader(name = "Authorization-API") String tokenApi) {

        // TODO : check if the user has the right of a teacher

        tokenApi = tokenApi.substring(7);
        Claims claims = jwtTokenUtil.parseClaims(tokenApi);

        List<TpResponse> tpResponses = tpService.getCompletionsByTpId(id);

        if (tpResponses != null && !tpResponses.isEmpty()) {
            return ResponseEntity.ok(tpResponses);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.emptyList());
        }
    }

    @PutMapping("/tp/myCompletion/{id}")
    public ResponseEntity<Void> updateMyCompletion(
            @PathVariable Integer id,
            @RequestHeader(name = "Authorization-API") String tokenApi,
            @RequestBody Integer updatedCompletion) {

        tokenApi = tokenApi.substring(7);
        Claims claims = jwtTokenUtil.parseClaims(tokenApi);
        String uniqueName = claims.get("unique_name").toString();

        tpService.updateCompletion(uniqueName, id, updatedCompletion);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/tp/timeElapsed/{id}")
    public ResponseEntity<Void> updateTimeElapsed(@PathVariable Integer id,
            @RequestHeader(name = "Authorization-API") String tokenApi, @RequestBody Integer timeElasped) {
        tokenApi = tokenApi.substring(7);
        Claims claims = jwtTokenUtil.parseClaims(tokenApi);
        String uniqueName = claims.get("unique_name").toString();

        tpService.updateTimeElapsed(uniqueName, id, timeElasped);

        return ResponseEntity.noContent().build();
    }
}
