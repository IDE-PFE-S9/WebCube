package fr.eseo.webcube.api.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {

    private String given_name;

    private String family_name;

    private String token;

    public AuthResponse(String given_name, String family_name){
        this.given_name = given_name;
        this.family_name = family_name;
    }

    public AuthResponse(String given_name, String family_name, String token){
        this.given_name = given_name;
        this.family_name = family_name;
        this.token = token;
    }


    
}
