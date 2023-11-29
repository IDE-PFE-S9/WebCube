package fr.eseo.webcube.api.controller;

import java.util.Date;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;

import fr.eseo.webcube.api.model.Role;
import fr.eseo.webcube.api.security.AuthResponse;
import fr.eseo.webcube.api.security.JwtTokenUtil;
import fr.eseo.webcube.api.security.UserTokenAzure;
import fr.eseo.webcube.api.service.UserService;
import io.jsonwebtoken.Claims;

@RestController
@RequestMapping("/User")
public class UserController {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    UserService userService;


    @GetMapping()
    public ResponseEntity<?> getUserDetails(@RequestHeader("Authorization") String token) {
        token = token.substring(7);
        Claims claims = jwtTokenUtil.parseClaims(token);
        String givenName = claims.get("given_name").toString();
        String family_name = claims.get("family_name").toString();
        //System.out.println(id);
        //System.out.println(rolePerson);
        //System.out.println(typePerson);
        UserTokenAzure response = new UserTokenAzure(givenName, family_name);
        return ResponseEntity.ok().body(response);
    }

    /*@GetMapping("/Exemple")
    public ResponseEntity<?> getUserExemple(@RequestHeader("Authorization") String token) {
        String givenName = "givenName";
        String family_name = "familyName";
        UserDetailsResponse response = new UserDetailsResponse(givenName, family_name);
        return ResponseEntity.ok().body(response);
    }*/

    @GetMapping("/Exemple")
    public ResponseEntity<?> getUserExemple(@RequestHeader("Authorization-Azure") String token) {
        
        token = token.substring(7);

        try {
            JWTClaimsSet claimsAzure = JWTParser.parse(token).getJWTClaimsSet();
            String firstName = (String) claimsAzure.getClaim("given_name");
            String lastName = (String) claimsAzure.getClaim("family_name");
            String uniqueName = (String) claimsAzure.getClaim("upn");
            Date expirationTokenAzure = (Date) claimsAzure.getClaim("exp");
            
            Set<Role> roles = userService.getRolesByUniqueName(uniqueName);
            UserTokenAzure user = new UserTokenAzure(firstName, lastName);
            user.setRole(roles);
            user.setExpirationToken(expirationTokenAzure);
            user.setUniqueName((uniqueName));
            String accesToken = jwtTokenUtil.generateAccessToken(user);

            AuthResponse response = new AuthResponse(firstName, lastName, accesToken);
            return ResponseEntity.ok().body(response);
        } catch (ParseException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur lors du traitement du token JWT.");
    }
}

    @GetMapping("/exemple/tokens")
    public ResponseEntity<?> readTokenExemple(
        @RequestHeader(name = "Authorization-Azure") String tokenAzure,
        @RequestHeader(name = "Authorization-API") String tokenApi) {
        
        tokenAzure = tokenAzure.substring(7);

        try {
            JWTClaimsSet claimsSet = JWTParser.parse(tokenAzure).getJWTClaimsSet();
            String firstName = (String) claimsSet.getClaim("given_name");
            String lastName = (String) claimsSet.getClaim("family_name");

            tokenApi = tokenApi.substring(7);

            Claims claims = jwtTokenUtil.parseClaims(tokenApi);

            String surname = claims.get("surname", String.class);
            String firstname = claims.get("firstname", String.class);
            Set<Role> roles = new HashSet<>(claims.get("roles", List.class));

            UserTokenAzure user = new UserTokenAzure(firstName, lastName);
            user.setRole(roles);

            AuthResponse response = new AuthResponse(firstName, lastName);
            return ResponseEntity.ok().body(response);
        } catch (ParseException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur lors du traitement du token JWT.");
        }
    }

    
}
