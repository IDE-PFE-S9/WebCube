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

import fr.eseo.webcube.api.Response.UserResponse;
import fr.eseo.webcube.api.model.Role;
import fr.eseo.webcube.api.security.AuthResponse;
import fr.eseo.webcube.api.security.JwtTokenUtil;
import fr.eseo.webcube.api.security.UserTokenAzure;
import fr.eseo.webcube.api.service.UserService;
import io.jsonwebtoken.Claims;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    UserService userService;

    @GetMapping("/auth")
    public ResponseEntity<?> azureAuth(@RequestHeader("Authorization-Azure") String token) {

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

    @GetMapping("/user")
    public ResponseEntity<UserResponse> userDetails(@RequestHeader(name = "Authorization-API") String tokenApi) {
        tokenApi = tokenApi.substring(7);
        Claims claims = jwtTokenUtil.parseClaims(tokenApi);

        String uniqueName = (String) claims.get("uniqueName");
        String firstName = (String) claims.get("firstname");
        String surname = (String) claims.get("surname");
        Set<Role> roles = new HashSet<>(claims.get("roles", List.class));
        UserResponse userResponse = new UserResponse(uniqueName, firstName, surname, roles);

        return ResponseEntity.ok().body(userResponse);

    }
}
