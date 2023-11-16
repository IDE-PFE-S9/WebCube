package fr.eseo.webcube.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

//Cette classe permet de créer le JWT token
@Component
public class JwtTokenUtil {
    
    @Value("${app.jwt.secret}")
    private String SECRET_KEY;

    @Value("spring.security.oauth2.client.registration.azure.client-secret")
    private String CLIENT_SECRET;

    public String generateAccessToken(UserTokenAzure user) {
        return Jwts.builder()
                .setSubject(String.format("%s", "webcube"))
                .claim("surname", user.getFamily_name())
                .claim("firstname", user.getGiven_name())
                .claim("roles", user.getRole())
                .setIssuer("ESEO")
                .setIssuedAt(new Date())
                .setExpiration(user.getExpirationToken())
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

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
