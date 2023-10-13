package fr.eseo.webcube.api.security;

import fr.eseo.webcube.api.model.Etudiant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

//Cette classe permet de créer le JWT token
@Component
public class JwtTokenUtil {
    private static final long EXPIRE_DURATION = (long) 24 * 60 * 60 * 1000;

    @Value("${app.jwt.secret}")
    private String SECRET_KEY;

    public String generateAccessToken(Etudiant user) {
        return Jwts.builder()
                .setSubject(String.format("%s", user.getMail()))
                .claim("id", user.getIdEtudiant())
                //.claim("role", user.getPermission())
                //.claim("email", user.getEmail())
                .setIssuer("ESEO")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION))
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