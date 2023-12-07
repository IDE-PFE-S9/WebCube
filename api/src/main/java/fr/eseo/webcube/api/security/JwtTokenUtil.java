package fr.eseo.webcube.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fr.eseo.webcube.api.model.User;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

//Cette classe permet de créer le JWT token
@Component
public class JwtTokenUtil {
    
    @Value("${app.jwt.secret}")
    private String SECRET_KEY;

    @Value("spring.security.oauth2.client.registration.azure.client-secret")
    private String CLIENT_SECRET;

    private static final long EXPIRE_COURTE = (long) 24 * 60 * 60 * 1000; // 1 minute

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(String.format("%s", "webcube"))
                .claim("surname", user.getSurname())
                .claim("firstname", user.getFirstname())
                .claim("roles", user.getRoles())
                .claim("unique_name", user.getUniqueName())
                .setIssuer("ESEO")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_COURTE))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public boolean validateAccessToken(String token, HttpServletResponse response) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex){
            System.out.println("Invalid JWT Signature");
        }catch (MalformedJwtException ex){
            System.out.println("Invalid JWT token");
        }catch (ExpiredJwtException ex){
            System.out.println("Expired JWT token");
            response.setHeader("Token-Status", "Expired");
        }catch (UnsupportedJwtException ex){
            System.out.println("Unsupported JWT exception");
        }catch (IllegalArgumentException ex){
            System.out.println("Jwt claims string is empty");
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
