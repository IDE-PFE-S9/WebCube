package fr.eseo.webcube.api.controller;

import fr.eseo.webcube.api.model.Etudiant;
import fr.eseo.webcube.api.security.AuthRequest;
import fr.eseo.webcube.api.security.AuthResponse;
import fr.eseo.webcube.api.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthenticationEndpoint {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenUtil jwtUtil;

    //Cette methode permet d'authentifier la personne et de lui envoyer son JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getLogin(), request.getPassword())
            );
            Etudiant user = (Etudiant) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(user);
            //String roleUser = user.getPermission().substring(5);
            //String userType = roleUser.substring(0, 1).toUpperCase() + roleUser.substring(1).toLowerCase();
            AuthResponse response = new AuthResponse(user.getUsername(), accessToken);//, userType);
            return ResponseEntity.ok().body(response);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    //TODO : Ajouter une methode pour se deconnecter ?
}
