package fr.eseo.webcube.api.security;

//Cette classe permet de créer les informations renvoyées après le login d'une personne sur le site

public class AuthResponse {

    private String login;
    private String accessToken;
    private String userType;

    public AuthResponse() {}

    public AuthResponse(String login, String accessToken) {
        this.login = login;
        this.accessToken = accessToken;
    }

    public AuthResponse(String login, String accessToken, String userType) {
        this.login = login;
        this.accessToken = accessToken;
        this.userType = userType;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
