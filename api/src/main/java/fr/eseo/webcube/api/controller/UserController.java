package fr.eseo.webcube.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.eseo.webcube.api.security.JwtTokenUtil;
import fr.eseo.webcube.api.security.UserDetailsResponse;
import io.jsonwebtoken.Claims;

@RestController
@RequestMapping("/User")
public class UserController {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @GetMapping()
    public ResponseEntity<?> getUserDetails(@RequestHeader("Authorization") String token) {
        token = token.substring(7);
        Claims claims = jwtTokenUtil.parseClaims(token);
        String givenName = claims.get("given_name").toString();
        String family_name = claims.get("family_name").toString();
        //System.out.println(id);
        //System.out.println(rolePerson);
        //System.out.println(typePerson);
        UserDetailsResponse response = new UserDetailsResponse(givenName, family_name);
        return ResponseEntity.ok().body(response);
    }
    
}
