package fr.eseo.webcube.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fr.eseo.webcube.api.model.User;

import java.util.Date;

//Cette classe permet de créer le JWT token
@Component
public class JwtTokenUtil {
    
    @Value("${app.jwt.secret}")
    private String SECRET_KEY;

    @Value("spring.security.oauth2.client.registration.azure.client-secret")
    private String CLIENT_SECRET;

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(String.format("%s", "webcube"))
                .claim("surname", user.getSurname())
                .claim("firstname", user.getFirstname())
                .claim("roles", user.getRoles())
                .claim("unique_name", user.getUniqueName())
                .setIssuer("ESEO")
                .setIssuedAt(new Date())
                //.setExpiration(user.getExpirationToken())
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // TODO à remplacer par un logger
        }
        return false;
    }

    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    public String getUniqueName(String token) {
        return parseClaims(token).get("unique_name", String.class);
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
