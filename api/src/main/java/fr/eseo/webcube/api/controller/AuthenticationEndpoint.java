package fr.eseo.webcube.api.controller;

import fr.eseo.webcube.api.model.Etudiant;
import fr.eseo.webcube.api.security.AuthRequest;
import fr.eseo.webcube.api.security.AuthResponse;
import fr.eseo.webcube.api.security.JwtTokenUtil;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.security.oauth2.core.OAuth2AccessToken;
//import org.springframework.security.oauth2.core.oidc.OidcIdToken;
//import org.springframework.security.oauth2.core.oidc.user.OidcUser;
//import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

/*@RestController
//@CrossOrigin
//@RequestMapping("/auth")
public class AuthenticationEndpoint {
    /*@Autowired
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
    }*/

    //TODO : Ajouter une methode pour se deconnecter ?

    /*private OAuth2AuthorizedClientService authorizedClientService;

	public AuthenticationEndpoint(OAuth2AuthorizedClientService authorizedClientService) {
		this.authorizedClientService = authorizedClientService;
	}

    @GetMapping("/*")
	public String getUserInfo(Principal user, @AuthenticationPrincipal OidcUser oidcUser) {
		StringBuffer userInfo = new StringBuffer();
		if (user instanceof UsernamePasswordAuthenticationToken) {
			userInfo.append(getUsernamePasswordLoginInfo(user));
		} else if (user instanceof OAuth2AuthenticationToken) {
			userInfo.append(getOauth2LoginInfo(user, oidcUser));
		}
		return userInfo.toString();
	}

    private StringBuffer getUsernamePasswordLoginInfo(Principal user) {
		StringBuffer usernameInfo = new StringBuffer();

		UsernamePasswordAuthenticationToken token = ((UsernamePasswordAuthenticationToken) user);
		if (token.isAuthenticated()) {
			User u = (User) token.getPrincipal();
			usernameInfo.append("Welcome, " + u.getUsername());
		} else {
			usernameInfo.append("NA");
		}
		return usernameInfo;
	}

	private StringBuffer getOauth2LoginInfo(Principal user, OidcUser oidcUser) {
		StringBuffer protectedInfo = new StringBuffer();

		OAuth2AuthenticationToken authToken = ((OAuth2AuthenticationToken) user);
		OAuth2AuthorizedClient authClient = this.authorizedClientService
				.loadAuthorizedClient(authToken.getAuthorizedClientRegistrationId(), authToken.getName());
		if (authToken.isAuthenticated()) {

			Map<String, Object> userAttributes = ((DefaultOAuth2User) authToken.getPrincipal()).getAttributes();

			String userToken = authClient.getAccessToken().getTokenValue();
			protectedInfo.append("Welcome, " + userAttributes.get("name") + "<br><br>");
			protectedInfo.append("e-mail: " + userAttributes.get("email") + "<br><br>");
			protectedInfo.append("Access Token: " + userToken + "<br><br>");

			if (oidcUser != null) {
				OidcIdToken idToken = oidcUser.getIdToken();
				if (idToken != null) {
					protectedInfo.append("idToken value: " + idToken.getTokenValue() + "<br><br>");
					protectedInfo.append("Token mapped values <br><br>");
					Map<String, Object> claims = idToken.getClaims();
					for (String key : claims.keySet()) {
						protectedInfo.append("  " + key + ": " + claims.get(key) + "<br>");
					}
				}
			}
		} else {
			protectedInfo.append("NA");
		}
		return protectedInfo;
	}*/

	/*@Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenUtil jwtUtil;

	@PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
		try{
        // Crée une authentification à partir du login et du mot de passe reçus
        Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getLogin(), request.getPassword())
            );

        // Met à jour le contexte de sécurité
        SecurityContextHolder.getContext().setAuthentication(authentication);

		Etudiant user = (Etudiant) authentication.getPrincipal();
		String accessToken = jwtUtil.generateAccessToken(user);
        // Génère et retourne un jeton OAuth2
        AuthResponse response = new AuthResponse(user.getUsername(), accessToken);//, userType);
            return ResponseEntity.ok().body(response);
		} catch (BadCredentialsException ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
    }
}*/
