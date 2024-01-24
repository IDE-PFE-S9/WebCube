package fr.eseo.webcube.api.controller;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;

import fr.eseo.webcube.api.Response.UserResponse;
import fr.eseo.webcube.api.model.Role;
import fr.eseo.webcube.api.model.User;
import fr.eseo.webcube.api.request.UpdateRoleRequest;
import fr.eseo.webcube.api.security.AuthResponse;
import fr.eseo.webcube.api.security.JwtTokenUtil;
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
        if (!isMicrosoftTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invalide");
        }
        try {
            token = token.substring(7);
            JWTClaimsSet claimsAzure = JWTParser.parse(token).getJWTClaimsSet();
            String firstName = (String) claimsAzure.getClaim("given_name");
            String lastName = (String) claimsAzure.getClaim("family_name");
            String uniqueName = (String) claimsAzure.getClaim("upn");

            Optional<User> user = userService.getUserByUniqueName(uniqueName);
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Utilisateur non trouvé dans la base de données");
            }

            String accesToken = jwtTokenUtil.generateAccessToken(user.get());

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

        String uniqueName = (String) claims.get("unique_name");
        String firstName = (String) claims.get("firstname");
        String surname = (String) claims.get("surname");
        Set<Role> roles = new HashSet<>(claims.get("roles", List.class));
        UserResponse userResponse = new UserResponse(uniqueName, firstName, surname, roles);

        return ResponseEntity.ok().body(userResponse);
    }

    private boolean isMicrosoftTokenValid(String accessToken){
        try{
            // Create a SimpleClientHttpRequestFactory object
            SimpleClientHttpRequestFactory clientHttpReq = new SimpleClientHttpRequestFactory();

            // Set the proxy
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.4.8", 3128));
            clientHttpReq.setProxy(proxy);

            // Initialize RestTemplate with the clientHttpReq
            RestTemplate restTemplate = new RestTemplate(clientHttpReq);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", accessToken);

            HttpEntity<String> entity = new HttpEntity<>(null, headers);

            String result = restTemplate.exchange("https://graph.microsoft.com/v1.0/me", HttpMethod.GET,
                    entity, String.class).getBody();
            return true;        
        }
        catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    @PutMapping("/update/role")
    public ResponseEntity<?> updateRoleUser(@RequestHeader(name = "Authorization-API") String tokenApi, 
                                            @RequestBody UpdateRoleRequest updateRoleRequest) {
        try {
            userService.updateRoles(updateRoleRequest.getUniqueName(), updateRoleRequest.getRole());
            return ResponseEntity.ok().body("Rôles mis à jour");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur lors de la mise à jour des rôles");
        }
    }

}